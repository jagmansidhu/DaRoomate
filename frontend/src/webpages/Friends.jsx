import React, {useEffect, useState} from 'react';
import {useAuth0} from '@auth0/auth0-react';
import { Check, X, UserMinus } from 'lucide-react';

const Friend = () => {
    const {getAccessTokenSilently, isAuthenticated, isLoading} = useAuth0();
    const [email, setEmail] = useState('');
    const [friendRequests, setFriendRequests] = useState([]);
    const [friendsList, setFriendsList] = useState([]); // New state for friends list
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isFetchingRequests, setIsFetchingRequests] = useState(true);
    const [isFetchingFriends, setIsFetchingFriends] = useState(true); // New state for friends loading
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

            const response = await fetch('http://localhost:8085/api/friend/addFriend', {
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
            const response = await fetch('http://localhost:8085/api/friend/request/pending', {
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
            const response = await fetch(`http://localhost:8085/api/friend/accept/${requestId}`, {
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
            const response = await fetch(`http://localhost:8085/api/friend/reject/${requestId}`, {
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
            const response = await fetch('http://localhost:8085/api/friend/getfriends', {
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
            const response = await fetch('http://localhost:8085/api/friend/remove', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ email: friendEmail })
            });

            if (response.ok) {
                setMessage({type: 'success', text: `Friend ${friendEmail} removed successfully!`});
                getFriendsList(); // Refresh the list of friends
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
        <div className="max-w-md mx-auto bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center mb-4">
                <h2 className="text-xl font-semibold text-gray-800">Send Friend Request</h2>
            </div>

            <div className="space-y-4">
                <div>
                    <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                        Friend's Email Address
                    </label>
                    <input
                        type="email"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="Enter email address"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
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
                    className="w-full flex items-center justify-center px-4 py-2 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                    {isSubmitting ? (
                        <>
                            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
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
                    <div className="flex items-center">
                        <p className={`text-sm ${
                            message.type === 'success' ? 'text-green-800' : 'text-red-800'
                        }`}>
                            {message.text}
                        </p>
                    </div>
                </div>
            )}
            <div className="mt-8">
                <div className="flex items-center mb-4">
                    <h2 className="text-xl font-semibold text-gray-800">Pending Friend Requests</h2>
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
                            <li key={request.id} className="flex items-center justify-between p-3 border border-gray-200 rounded-md bg-gray-50 shadow-sm">
                                <p className="text-gray-800 font-medium">
                                    {request.requester?.firstName} {request.requester?.lastName} ({request.requester?.email})
                                </p>
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => handleAcceptRequest(request.id)}
                                        className="p-2 bg-green-500 text-white rounded-full hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-500 transition-colors"
                                        title="Accept Request"
                                    >
                                        <Check className="h-5 w-5"/>
                                    </button>
                                    <button
                                        onClick={() => handleDeclineRequest(request.id)}
                                        className="p-2 bg-red-500 text-white rounded-full hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 transition-colors"
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

            <div className="mt-8">
                <div className="flex items-center mb-4">
                    <h2 className="text-xl font-semibold text-gray-800">My Friends</h2>
                </div>

                {isFetchingFriends ? (
                    <div>
                        <p>Loading friends...</p>
                    </div>
                ) : friendsList.length === 0 ? (
                    <p className="text-gray-600 italic">You have no friends yet.</p>
                ) : (
                    <ul className="space-y-3">
                        {friendsList.map((friend) => (
                            <li key={friend.id} className="flex items-center justify-between p-3 border border-gray-200 rounded-md bg-gray-50 shadow-sm">
                                <p className="text-gray-800 font-medium">
                                    {friend.firstName} {friend.lastName} ({friend.email})
                                </p>
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => handleRemoveFriend(friend.email)}
                                        className="p-2 bg-red-500 text-white rounded-full hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 transition-colors"
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
