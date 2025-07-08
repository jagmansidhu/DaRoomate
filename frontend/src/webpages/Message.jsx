import React, {useCallback, useEffect, useRef, useState} from "react";
import SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";
import axios from "axios";
import {useAuth0} from "@auth0/auth0-react";
import {FaFile, FaImage, FaPaperPlane, FaTimesCircle, FaTrash} from 'react-icons/fa';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8085';

const Message = () => {
    const { getAccessTokenSilently, user, isLoading, isAuthenticated } = useAuth0();

    const [error, setError] = useState(null);
    const [apiLoading, setApiLoading] = useState(true);
    const [accessToken, setAccessToken] = useState(null);

    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");
    const [availableChatPartners, setAvailableChatPartners] = useState([]);
    const [selectedRecipient, setSelectedRecipient] = useState(null);
    const [unreadCounts, setUnreadCounts] = useState({});
    const [selectedFile, setSelectedFile] = useState(null);

    const stompClientRef = useRef(null);
    const messagesEndRef = useRef(null);
    const fileInputRef = useRef(null);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth", block: "end" });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    useEffect(() => {
        const fetchInitialData = async () => {
            if (!isAuthenticated || isLoading) return;

            try {
                const token = await getAccessTokenSilently({
                    authorizationParams: {
                        audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                        scope: 'read:data',
                    },
                });
                setAccessToken(token);

                await axios.get(`${API_BASE_URL}/api/create_or_find_user`, {
                    headers: { Authorization: `Bearer ${token}` },
                });

                const allUsersResponse = await axios.get(`${API_BASE_URL}/api/messages/users/${user.email}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                const allChattableUsers = allUsersResponse.data.filter(u => u.email !== user.email);

                const chatsResponse = await axios.get(`${API_BASE_URL}/api/messages/chats/${user.email}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                const existingChatPartners = new Set(chatsResponse.data);

                const combinedPartnersMap = new Map();
                allChattableUsers.forEach(u => combinedPartnersMap.set(u.email, u));
                existingChatPartners.forEach(email => {
                    if (typeof email === 'string' && email !== user.email && !combinedPartnersMap.has(email)) {
                        combinedPartnersMap.set(email, { email });
                    }
                });

                console.log("All chat partners final list:", Array.from(combinedPartnersMap.keys()));


                //
                // const combinedPartnersMap = new Map();
                //
                // existingChatPartners.forEach(email => {
                //     if (typeof email === 'string') {
                //         combinedPartnersMap.set(email, { email });
                //     }
                // });
                //
                // allChattableUsers.forEach(u => {
                //     combinedPartnersMap.set(u.email, u);
                // });


                setAvailableChatPartners(Array.from(combinedPartnersMap.values()));

                const unreadResponse = await axios.get(`${API_BASE_URL}/api/messages/unread/${user.email}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setUnreadCounts(unreadResponse.data || {});

                const localStorageKey = `selectedRecipient_${user.email}`;
                const savedRecipient = localStorage.getItem(localStorageKey);

                if (savedRecipient && combinedPartnersMap.has(savedRecipient)) {
                    setSelectedRecipient(savedRecipient);
                } else if (allChattableUsers.length > 0) {
                    setSelectedRecipient(allChattableUsers[0].email);
                }

            } catch (err) {
                console.error('Error fetching initial data:', err);
                setError(err);
            } finally {
                setApiLoading(false);
            }
        };
        fetchInitialData();
    }, [getAccessTokenSilently, isAuthenticated, isLoading, user]);

    useEffect(() => {
        if (selectedRecipient && user?.email) {
            const localStorageKey = `selectedRecipient_${user.email}`;
            localStorage.setItem(localStorageKey, selectedRecipient);
        }
    }, [selectedRecipient, user]);


    useEffect(() => {
        if (!user || !user.email || !selectedRecipient || !accessToken) return;

        const fetchMessages = async () => {
            try {
                const response = await axios.get(
                    `${API_BASE_URL}/api/messages/${user.email}/${selectedRecipient}`,
                    { headers: { Authorization: `Bearer ${accessToken}` } }
                );
                console.log(`FETCH: Fetched ${response.data.length} messages for chat with ${selectedRecipient}. First message ID: ${response.data.length > 0 ? response.data[0].id : 'N/A'}`);
                setMessages(response.data);

                const unreadMsgsFromRecipient = response.data.filter(
                    message => !message.isRead && message.recipientId === user.email && message.senderId === selectedRecipient
                );

                if (unreadMsgsFromRecipient.length > 0) {
                    await axios.put(`${API_BASE_URL}/api/messages/read/bulk`, null, {
                        headers: { Authorization: `Bearer ${accessToken}` },
                        params: {
                            senderId: selectedRecipient,
                            recipientId: user.email
                        }
                    }).catch(err => console.error(`Error marking messages from ${selectedRecipient} as read in bulk:`, err));

                    setUnreadCounts(prev => ({
                        ...prev,
                        [selectedRecipient]: 0
                    }));
                }

            } catch (err) {
                console.error('Error fetching messages:', err);
            }
        };
        fetchMessages();
    }, [user, selectedRecipient, accessToken]);

    useEffect(() => {
        if (!user || !user.email) return;

        const socket = new SockJS(`${API_BASE_URL}/ws`);
        const stompClient = Stomp.over(socket);
        stompClient.reconnectDelay = 5000;
        stompClientRef.current = stompClient;

        stompClient.connect({}, () => {
            console.log("STOMP Connected!");
            console.log(`STOMP Subscribing to: /user/${user.email}/queue/messages`);

            stompClient.subscribe(`/user/${user.email}/queue/messages`, (messageOutput) => {
                const msg = JSON.parse(messageOutput.body);
                console.log("--- WS Message Received ---");
                console.log("WS: Raw message payload:", msg);
                console.log("WS: Recipient email for subscription (this client):", user.email);
                console.log("WS: Sender of received message (from payload):", msg.senderId);
                console.log("WS: Recipient of received message (from payload):", msg.recipientId);

                setMessages(prevMessages => {
                    console.log("WS: Current messages state IDs before update:", prevMessages.map(m => m.id));
                    const incomingMessageId = msg.id;
                    const isOwnMessage = msg.senderId === user.email;

                    if (prevMessages.some(m => m.id === incomingMessageId)) {
                        console.log(`WS: Message ID ${incomingMessageId} already exists in state (from fetch/prev WS). Skipping add to display.`);
                        return prevMessages;
                    }

                    if (isOwnMessage && incomingMessageId && !incomingMessageId.startsWith('temp-')) {
                        const updatedMessages = prevMessages.map(m => {
                            if (m.id && m.id.startsWith('temp-') &&
                                m.senderId === msg.senderId &&
                                m.recipientId === msg.recipientId &&
                                m.content === msg.content
                            ) {
                                console.log(`WS: Replacing optimistic message ${m.id} with real message ${incomingMessageId}.`);
                                return msg;
                            }
                            return m;
                        });

                        if (updatedMessages.some(m => m.id === incomingMessageId)) {
                            console.log("WS: Optimistic message successfully replaced. New state IDs:", updatedMessages.map(m => m.id));
                            return updatedMessages;
                        }
                    }

                    if (msg.recipientId === user.email && msg.senderId === selectedRecipient) {
                        console.log(`WS: Adding new incoming message ${incomingMessageId} for currently selected recipient.`);
                        return [...prevMessages, msg];
                    }

                    console.log(`WS: Message ${incomingMessageId} is for a different recipient or already handled. Not adding to current display.`);
                    return prevMessages;
                });


                if (msg.recipientId === user.email && msg.senderId === selectedRecipient) {
                    axios.put(
                        `${API_BASE_URL}/api/messages/read/bulk`,
                        null,
                        {
                            headers: { Authorization: `Bearer ${accessToken}` },
                            params: {
                                senderId: selectedRecipient,
                                recipientId: user.email
                            }
                        }
                    ).catch(err => console.error(`Error marking received message ${msg.id} as read:`, err));

                    setUnreadCounts(prev => ({
                        ...prev,
                        [selectedRecipient]: 0
                    }));
                } else if (msg.recipientId === user.email) {
                    setUnreadCounts(prev => ({
                        ...prev,
                        [msg.senderId]: (prev[msg.senderId] || 0) + 1
                    }));
                }
            }, { Authorization: `Bearer ${accessToken}` });

        }, (error) => {
            console.error("STOMP Connection Error:", error);
            setError(new Error("WebSocket connection failed. Please try again."));
        });

        return () => {
            if (stompClientRef.current?.connected) {
                console.log("STOMP Disconnecting...");
                stompClientRef.current.disconnect();
            }
        };
    }, [user, accessToken]);

    const handleFileSelect = (event) => {
        const file = event.target.files[0];
        if (file) {
            setSelectedFile(file);
            setInput(`File selected: ${file.name}`);
        }
    };

    const clearSelectedFile = () => {
        setSelectedFile(null);
        setInput("");
        if (fileInputRef.current) {
            fileInputRef.current.value = '';
        }
    };

    const sendMessage = async () => {
        if ((!input.trim() && !selectedFile) || !selectedRecipient || !stompClientRef.current?.connected) {
            console.warn("Attempted to send empty message, no recipient, or disconnected STOMP client.");
            return;
        }

        let tempFrontendId;

        try {
            let messageContent = input.trim();
            let messageType = "TEXT";
            let attachmentUrl = null;
            let attachmentName = null;
            let attachmentSize = null;

            if (selectedFile) {
                const formData = new FormData();
                formData.append('file', selectedFile);

                const uploadResponse = await axios.post(
                    `${API_BASE_URL}/api/upload`,
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                            Authorization: `Bearer ${accessToken}`
                        }
                    }
                );

                attachmentUrl = uploadResponse.data.url;
                attachmentName = selectedFile.name;
                attachmentSize = selectedFile.size;
                messageType = selectedFile.type.startsWith('image/') ? 'IMAGE' : 'FILE';
                messageContent = selectedFile.type.startsWith('image/') ? '📷 Image' : '📎 File';
            }

            tempFrontendId = `temp-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;

            const messagePayloadForBackend = {
                senderId: user.email,
                recipientId: selectedRecipient,
                content: messageContent,
                messageType,
                attachmentUrl,
                attachmentName,
                attachmentSize,
                timestamp: new Date().toISOString(),
                isRead: false,
            };

            setMessages(prev => [...prev, { ...messagePayloadForBackend, id: tempFrontendId }]);

            console.log("Sending STOMP message from sender client (payload to backend):", messagePayloadForBackend);
            stompClientRef.current.send("/app/chat", {}, JSON.stringify(messagePayloadForBackend));

            setInput("");
            clearSelectedFile();
        } catch (err) {
            console.error('Error sending message or uploading file:', err);
            setError(new Error("Failed to send message. Please try again."));
            if (tempFrontendId) {
                setMessages(prev => prev.filter(msg => msg.id !== tempFrontendId));
            }
        }
    };

    const deleteMessage = async (messageId) => {
        if (!accessToken) return;
        try {
            await axios.delete(
                `${API_BASE_URL}/api/messages/${messageId}`,
                { headers: { Authorization: `Bearer ${accessToken}` } }
            );
            setMessages(prev => prev.filter(msg => msg.id !== messageId));
        } catch (err) {
            console.error('Error deleting message:', err);
            setError(new Error("Failed to delete message."));
        }
    };

    const renderMessage = useCallback((msg) => {
        const isOwnMessage = msg.senderId === user.email;
        const messageClass = isOwnMessage ? 'own-message' : 'other-message';
        return (
            <div key={msg.id} className={`message ${messageClass}`}>
                <div className="message-header">
                    <span className="sender">{isOwnMessage ? 'You' : msg.senderId.split('@')[0]}</span>
                    <span className="timestamp">
                        {msg.timestamp ? new Date(msg.timestamp).toLocaleTimeString() : 'Sending...'}
                    </span>
                    {isOwnMessage && (
                        <button
                            className="delete-btn"
                            onClick={() => deleteMessage(msg.id)}
                            aria-label="Delete message"
                        >
                            <FaTrash />
                        </button>
                    )}
                </div>
                <div className="message-content">
                    {msg.messageType === 'IMAGE' ? (
                        msg.attachmentUrl ? (
                            <img src={msg.attachmentUrl} alt="Shared image" className="message-image" />
                        ) : (
                            <p>{msg.content}</p>
                        )
                    ) : msg.messageType === 'FILE' ? (
                        msg.attachmentUrl ? (
                            <a href={msg.attachmentUrl} download={msg.attachmentName || 'download'} className="file-link">
                                <FaFile /> {msg.attachmentName || 'Attachment'} ({msg.attachmentSize ? (msg.attachmentSize / 1024).toFixed(2) + ' KB' : ''})
                            </a>
                        ) : (
                            <p>{msg.content}</p>
                        )
                    ) : (
                        <p>{msg.content}</p>
                    )}
                </div>
                {isOwnMessage && msg.isRead && (
                    <span className="read-status">Read</span>
                )}
                {!isOwnMessage && !msg.isRead && msg.recipientId === user.email && (
                    <span className="unread-indicator">New</span>
                )}
            </div>
        );
    }, [user.email, deleteMessage]);

    if (apiLoading) return <div className="loading">Loading chat...</div>;
    if (error) return <div className="error">Error: {error.message}</div>;

    return (
        <div className="chat-container">
            <div className="chat-sidebar">
                <h3>Chats</h3>
                <div className="user-list">
                    {availableChatPartners.length > 0 ? (
                        availableChatPartners.map((partner) => (
                            <div
                                key={partner.email}
                                className={`user-item ${selectedRecipient === partner.email ? 'selected' : ''}`}
                                onClick={() => setSelectedRecipient(partner.email)}
                            >
                                <span className="user-name">{partner.email.split('@')[0]}</span>
                                {unreadCounts[partner.email] > 0 && (
                                    <span className="unread-badge">{unreadCounts[partner.email]}</span>
                                )}
                            </div>
                        ))
                    ) : (
                        <p className="no-chats">No other users available to chat with.</p>
                    )}
                </div>
            </div>

            <div className="chat-main">
                <div className="chat-header">
                    <h2>{selectedRecipient ? `Chatting with ${selectedRecipient.split('@')[0]}` : 'Select a user to start chatting'}</h2>
                </div>

                <div className="messages-container">
                    {messages.length > 0 ? (
                        messages.map(renderMessage)
                    ) : (
                        <div className="no-messages">
                            {selectedRecipient ? (
                                <p>No messages yet. Start a conversation!</p>
                            ) : (
                                <p>Select a user from the left sidebar to start chatting.</p>
                            )}
                        </div>
                    )}
                    <div ref={messagesEndRef} />
                </div>

                <div className="message-input-area">
                    {selectedFile && (
                        <div className="file-preview-strip">
                            <span>{selectedFile.name}</span>
                            <button onClick={clearSelectedFile} className="clear-file-btn" aria-label="Clear selected file">
                                <FaTimesCircle />
                            </button>
                        </div>
                    )}
                    <div className="message-input">
                        <input
                            type="file"
                            ref={fileInputRef}
                            onChange={handleFileSelect}
                            style={{ display: 'none' }}
                            accept="image/*,.pdf,.doc,.docx,.txt"
                        />
                        <button
                            className="icon-button file-btn"
                            onClick={() => fileInputRef.current?.click()}
                            disabled={!selectedRecipient}
                            aria-label="Attach file"
                        >
                            <FaImage />
                        </button>
                        <input
                            type="text"
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={(e) => e.key === "Enter" && sendMessage()}
                            placeholder={selectedRecipient ? `Message ${selectedRecipient.split('@')[0]}...` : "Select a user to chat"}
                            disabled={!selectedRecipient}
                            aria-label="Type your message"
                        />
                        <button
                            className="icon-button send-btn"
                            onClick={sendMessage}
                            disabled={!selectedRecipient || (!input.trim() && !selectedFile)}
                            aria-label="Send message"
                        >
                            <FaPaperPlane />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Message;
