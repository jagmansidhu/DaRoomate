import React, {useState, useEffect, useRef} from "react";
import SockJS from "sockjs-client";
import {CompatClient, Stomp} from "@stomp/stompjs";
import axios from "axios";
import {useAuth0} from "@auth0/auth0-react";

const auth0UserId = localStorage.getItem("auth0_sub");

const Message = () => {

    const {getAccessTokenSilently, user, isLoading, isAuthenticated} = useAuth0();
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [apiLoading, setApiLoading] = useState(true);
    const [accessToken, setAccessToken] = useState(null);
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");
    const stompClientRef = useRef(null);

    useEffect(() => {
        const fetchProtectedData = async () => {
            async function getToken() {
                try {
                    const accessToken = await getAccessTokenSilently({
                        authorizationParams: {
                            audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                            scope: 'read:data',
                        },
                    });
                    setAccessToken(accessToken);

                    const response = await axios.get('http://localhost:8085/api/create_or_find_user', {
                        headers: {
                            Authorization: `Bearer ${accessToken}`,
                        },
                    });
                    setData(response.data);
                } catch (err) {
                    console.error('Error fetching protected resource:', err);
                    setError(err);
                } finally {
                    setApiLoading(false);
                }
            }

            await getToken();
        }
        fetchProtectedData();
    }, [getAccessTokenSilently, isAuthenticated, isLoading, user, setAccessToken]);

    useEffect(() => {
        console.log("MONKE" + user.email);

        const socket = new SockJS("http://localhost:8085/ws");
        const stompClient = Stomp.over(socket);
        stompClientRef.current = stompClient;

        stompClient.connect({}, () => {
            stompClient.subscribe("/topic/public", (messageOutput) => {
                const msg = JSON.parse(messageOutput.body);
                setMessages((prev) => [...prev, msg]);
            });

            stompClient.send("/app/chat.addUser", {}, JSON.stringify({
                messengerId: user.name,
                messageType: "JOIN"
            }));
        }, (error) => {
            console.error("STOMP connection error:", error);
        });

        return () => {
            if (stompClientRef.current && stompClientRef.current.connected) {
                stompClientRef.current.disconnect(() => {
                    console.log("STOMP client disconnected.");
                });
            }
        };
    }, []);

    const sendMessage = () => {
        if (input.trim() === "" || !stompClientRef.current || !stompClientRef.current.connected) {
            console.warn("Cannot send message: input is empty or STOMP client not connected.");
            return;
        }

        const chatMessage = {
            messengerId: user.name,
            message: input,
            messageType: "CHAT"
        };

        stompClientRef.current.send("/app/chat.send", {}, JSON.stringify(chatMessage));
        setInput("");
    };

    return (
        <div className="flex flex-col max-w-xl mx-auto p-4 bg-white shadow-lg rounded-lg">
            <h2 className="text-2xl font-bold mb-4 text-center text-gray-800">Chat Room</h2>
            <div className="border border-gray-300 rounded-md p-4 mb-4 flex-grow overflow-y-auto h-96 bg-gray-50">
                {messages.map((msg, index) => (
                    <div key={index} className="mb-2 last:mb-0">
                        {msg.messageType === "JOIN" ? (
                            <p className="text-green-600 font-semibold text-sm">
                                {msg.messenger} has joined the chat.
                                {msg.timestamp && <span
                                    className="text-gray-500 text-xs ml-2">({new Date(msg.timestamp).toLocaleTimeString()})</span>}
                            </p>
                        ) : msg.messageType === "LEAVE" ? (
                            <p className="text-red-600 font-semibold text-sm">
                                {msg.messenger} has left the chat.
                                {msg.timestamp && <span
                                    className="text-gray-500 text-xs ml-2">({new Date(msg.timestamp).toLocaleTimeString()})</span>}
                            </p>
                        ) : (
                            <p className="text-gray-800">
                                <strong className="text-blue-700">{msg.messenger}</strong>: {msg.message}
                                {msg.timestamp && <span
                                    className="text-gray-500 text-xs ml-2">({new Date(msg.timestamp).toLocaleTimeString()})</span>}
                            </p>
                        )}
                    </div>
                ))}
            </div>
            <div className="flex">
                <input
                    type="text"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    onKeyDown={(e) => e.key === "Enter" && sendMessage()}
                    placeholder="Type your message..."
                    className="flex-grow p-2 border border-gray-300 rounded-l-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <button
                    onClick={sendMessage}
                    className="px-4 py-2 bg-blue-600 text-white rounded-r-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                >
                    Send
                </button>
            </div>
        </div>
    );
};

export default Message;
