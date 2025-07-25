import React, {useEffect, useState} from 'react';
import {useAuth0} from '@auth0/auth0-react';
import { Check, X, UserMinus } from 'lucide-react';

const Friend = () => {
    const {getAccessTokenSilently, isAuthenticated, isLoading} = useAuth0();
    const [email, setEmail] = useState('');
    const [friendRequests, setFriendRequests] = useState([]);
    const [friendsList, setFriendsList] = useState([]);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isFetchingRequests, setIsFetchingRequests] = useState(true);
    const [isFetchingFriends, setIsFetchingFriends] = useState(true);
    const [message, setMessage] = useState({type: '', text: ''});

    const sendFriendRequest = async (e) => {
        e.preventDefault();

        if (!email.trim()) {
            setMessage({type: 'error', text: 'Please enter an email address'});
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            setMessage({type: 'error', text: 'Please enter a valid email address'});
            return;
        }

        setIsSubmitting(true);
        setMessage({type: '', text: ''});

        try {
            const token = await getAccessTokenSilently();

            const response = await fetch(`${process.env.REACT_APP_BASE_API_URL}/api/friend/addFriend`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({email})
            });

            if (response.ok) {
                setMessage({
                    type: 'success',
                    text: `Friend request sent successfully to ${email}!`
                });
                setEmail('');
                getPendingFriendRequest();

            } else {
                const errorData = await response.json();
                setMessage({
                    type: 'error',
                    text: errorData.message || errorData.error || 'An error occurred while sending the friend request.'
                });
            }
        } catch (error) {
            console.error('Error sending friend request:', error);
            setMessage({
                type: 'error',
                text: 'Network error. Please check your connection and try again.'
            });
        } finally {
            setIsSubmitting(false);
        }
    };

    const getPendingFriendRequest = async () => {
        setIsFetchingRequests(true);
        try {
            const token = await getAccessTokenSilently();
            const response = await fetch(`${process.env.REACT_APP_BASE_API_URL}/api/friend/request/pending`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const data = await response.json();
                setFriendRequests(data);
                setMessage({ type: 'success', text: 'Friend requests loaded successfully!' });
            } else {
                const errorData = await response.json();
                setMessage({
                    type: 'error',
                    text: errorData.message || 'Failed to load friend requests.'
                });
                setFriendRequests([]);
            }
        } catch (error) {
            console.error('Error fetching pending friend requests:', error);
            setMessage({
                type: 'error',
                text: 'Network error. Please check your connection and try again.'
            });
            setFriendRequests([]);
        } finally {
            setIsFetchingRequests(false);
        }
    };

    const handleAcceptRequest = async (requestId) => {
        setMessage({type: '', text: ''});
        try {
            const token = await getAccessTokenSilently();
            const response = await fetch(`${process.env.REACT_APP_BASE_API_URL}/api/friend/accept/${requestId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                setMessage({type: 'success', text: 'Friend request accepted!'});
                getPendingFriendRequest();
                getFriendsList();
            } else {
                const errorData = await response.json();
                setMessage({type: 'error', text: errorData.message || 'Failed to accept friend request.'});
            }
        } catch (error) {
            console.error('Error accepting friend request:', error);
            setMessage({type: 'error', text: 'Network error. Please try again.'});
        }
    };

    const handleDeclineRequest = async (requestId) => {
        setMessage({type: '', text: ''});
        try {
            const token = await getAccessTokenSilently();
            const response = await fetch(`${process.env.REACT_APP_BASE_API_URL}/api/friend/reject/${requestId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                setMessage({type: 'success', text: 'Friend request declined!'});
                getPendingFriendRequest();
            } else {
                const errorData = await response.json();
                setMessage({type: 'error', text: errorData.message || 'Failed to decline friend request.'});
            }
        } catch (error) {
            console.error('Error declining friend request:', error);
            setMessage({type: 'error', text: 'Network error. Please try again.'});
        }
    };

    const getFriendsList = async () => {
        setIsFetchingFriends(true);
        try {
            const token = await getAccessTokenSilently();
            const response = await fetch(`${process.env.REACT_APP_BASE_API_URL}/api/friend/getfriends`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const data = await response.json();
                setFriendsList(data);
                setMessage({ type: 'success', text: 'Friends list loaded successfully!' });
            } else {
                const errorData = await response.json();
                setMessage({
                    type: 'error',
                    text: errorData.message || 'Failed to load friends list.'
                });
                setFriendsList([]);
            }
        } catch (error) {
            console.error('Error fetching friends list:', error);
            setMessage({
                type: 'error',
                text: 'Network error. Please check your connection and try again.'
            });
            setFriendsList([]);
        } finally {
            setIsFetchingFriends(false);
        }
    };

    // New function to remove a friend
    const handleRemoveFriend = async (friendEmail) => {
        setMessage({type: '', text: ''});
        try {
            const token = await getAccessTokenSilently();
            const response = await fetch(`${process.env.REACT_APP_BASE_API_URL}/api/friend/remove`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ email: friendEmail })
            });

            if (response.ok) {
                setMessage({type: 'success', text: `Friend ${friendEmail} removed successfully!`});
                getFriendsList();
            } else {
                const errorData = await response.json();
                setMessage({type: 'error', text: errorData.message || 'Failed to remove friend.'});
            }
        } catch (error) {
            console.error('Error removing friend:', error);
            setMessage({type: 'error', text: 'Network error. Please try again.'});
        }
    };


    useEffect(() => {
        if (isAuthenticated) {
            getPendingFriendRequest();
            getFriendsList();
        } else {
            setFriendRequests([]);
            setFriendsList([]);
            setIsFetchingRequests(false);
            setIsFetchingFriends(false);
        }
    }, [isAuthenticated, getAccessTokenSilently]);

    if (isLoading) {
        return <p>Loading authentication...</p>;
    }

    return (
        <div>
            <div>
                <h2>Send Friend Request</h2>
            </div>

            <div>
                <div>
                    <label htmlFor="email">
                        Friend's Email Address
                    </label>
                    <input
                        type="email"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="Enter email address"
                        disabled={isSubmitting}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') {
                                sendFriendRequest(e);
                            }
                        }}
                    />
                </div>

                <button
                    onClick={sendFriendRequest}
                    disabled={isSubmitting || !email.trim()}
                >
                    {isSubmitting ? (
                        <>
                            <div></div>
                            Sending...
                        </>
                    ) : (
                        <>
                            Send Friend Request
                        </>
                    )}
                </button>
            </div>

            {message.text && (
                <div className={`mt-4 p-3 rounded-md ${
                    message.type === 'success'
                        ? 'bg-green-50 border border-green-200'
                        : 'bg-red-50 border border-red-200'
                }`}>
                    <div>
                        <p className={`text-sm ${
                            message.type === 'success' ? 'text-green-800' : 'text-red-800'
                        }`}>
                            {message.text}
                        </p>
                    </div>
                </div>
            )}
            <div>
                <div>
                    <h2>Pending Friend Requests</h2>
                </div>

                {isFetchingRequests ? (
                    <div>
                        <p>Loading requests...</p>
                    </div>
                ) : friendRequests.length === 0 ? (
                    <p className="text-gray-600 italic">No pending friend requests.</p>
                ) : (
                    <ul className="space-y-3">
                        {friendRequests.map((request) => (
                            <li key={request.id}>
                                <p>
                                    {request.requester?.firstName} {request.requester?.lastName} ({request.requester?.email})
                                </p>
                                <div>
                                    <button
                                        onClick={() => handleAcceptRequest(request.id)}
                                        title="Accept Request"
                                    >
                                        <Check className="h-5 w-5"/>
                                    </button>
                                    <button
                                        onClick={() => handleDeclineRequest(request.id)}
                                        title="Decline Request"
                                    >
                                        <X className="h-5 w-5"/>
                                    </button>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </div>

            <div>
                <div>
                    <h2>My Friends</h2>
                </div>

                {isFetchingFriends ? (
                    <div>
                        <p>Loading friends...</p>
                    </div>
                ) : friendsList.length === 0 ? (
                    <p>You have no friends yet.</p>
                ) : (
                    <ul className="space-y-3">
                        {friendsList.map((friend) => (
                            <li key={friend.id}>
                                <p>
                                    {friend.firstName} {friend.lastName} ({friend.email})
                                </p>
                                <div>
                                    <button
                                        onClick={() => handleRemoveFriend(friend.email)}
                                        title="Remove Friend"
                                    >
                                        <UserMinus className="h-5 w-5"/>
                                    </button>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
};

export default Friend;
