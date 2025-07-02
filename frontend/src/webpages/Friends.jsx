import React, {useEffect, useState} from 'react';
import {useAuth0} from '@auth0/auth0-react';

const Friend = () => {
    const {getAccessTokenSilently, isAuthenticated, isLoading} = useAuth0();
    const [email, setEmail] = useState('');
    const [friendRequests, setFriendRequests] = useState([]);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isFetchingRequests, setIsFetchingRequests] = useState(true);
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

            } else {
                if (response.status === 400) {
                    setMessage({
                        type: 'error',
                        text: 'Unable to send friend request. Please check the email and try again.'
                    });
                } else {
                    setMessage({
                        type: 'error',
                        text: 'An error occurred while sending the friend request.'
                    });
                }
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

    const getPendingFriendRequest = async (e) => {
        try {
            const token = await getAccessTokenSilently();
            const response = await fetch('http://localhost:8085/api/friend/request/pending', {
                method: 'Get',
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
                if (response.status === 400) {
                    setMessage({
                        type: 'error',
                        text: 'Unable to send friend request. Please check the email and try again.'
                    });
                } else {
                    setMessage({
                        type: 'error',
                        text: 'An error occurred while sending the friend request.'
                    });
                }
            }
        } catch (error) {
            console.error('Error sending friend request:', error);
            setMessage({
                type: 'error',
                text: 'Network error. Please check your connection and try again.'
            });
        } finally {
            setIsFetchingRequests(false);
        }

    };

    useEffect(() => {
        if (isAuthenticated) {
            getPendingFriendRequest();
        }
        else {
            setFriendRequests([]);
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
                    <div className="flex items-center">
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
                                {/*<div className="flex space-x-2">*/}
                                {/*    <button*/}
                                {/*        onClick={() => handleAcceptRequest(request.id, request.senderId)}*/}
                                {/*        className="p-2 bg-green-500 text-white rounded-full hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-500 transition-colors"*/}
                                {/*        title="Accept Request"*/}
                                {/*    >*/}
                                {/*        <Check className="h-5 w-5"/>*/}
                                {/*    </button>*/}
                                {/*    <button*/}
                                {/*        onClick={() => handleDeclineRequest(request.id)}*/}
                                {/*        className="p-2 bg-red-500 text-white rounded-full hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 transition-colors"*/}
                                {/*        title="Decline Request"*/}
                                {/*    >*/}
                                {/*        <X className="h-5 w-5"/>*/}
                                {/*    </button>*/}
                                {/*</div>*/}
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>

    );

};

export default Friend;