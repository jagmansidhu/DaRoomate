import React, { useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { Send, UserPlus, Check, X, AlertCircle } from 'lucide-react';

const SendFriendRequest = () => {
    const { getAccessTokenSilently, isAuthenticated, isLoading } = useAuth0();
    const [email, setEmail] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });

    const sendFriendRequest = async (e) => {
        e.preventDefault();

        if (!email.trim()) {
            setMessage({ type: 'error', text: 'Please enter an email address' });
            return;
        }

        // Basic email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            setMessage({ type: 'error', text: 'Please enter a valid email address' });
            return;
        }

        setIsSubmitting(true);
        setMessage({ type: '', text: '' });

        try {
            const token = await getAccessTokenSilently();

            const response = await fetch('http://localhost:8085/api/friend/addFriend', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ email })
            });

            if (response.ok) {
                const friendRequest = await response.json();
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

    // Show loading state while Auth0 is initializing
    if (isLoading) {
        return (
            <div className="flex items-center justify-center p-6">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    // Show message if user is not authenticated
    if (!isAuthenticated) {
        return (
            <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                <div className="flex items-center">
                    <AlertCircle className="h-5 w-5 text-yellow-600 mr-2" />
                    <p className="text-yellow-800">Please log in to send friend requests.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-md mx-auto bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center mb-4">
                <UserPlus className="h-6 w-6 text-blue-600 mr-2" />
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
                            <Send className="h-4 w-4 mr-2" />
                            Send Friend Request
                        </>
                    )}
                </button>
            </div>

            {/* Message Display */}
            {message.text && (
                <div className={`mt-4 p-3 rounded-md ${
                    message.type === 'success'
                        ? 'bg-green-50 border border-green-200'
                        : 'bg-red-50 border border-red-200'
                }`}>
                    <div className="flex items-center">
                        {message.type === 'success' ? (
                            <Check className="h-5 w-5 text-green-600 mr-2" />
                        ) : (
                            <X className="h-5 w-5 text-red-600 mr-2" />
                        )}
                        <p className={`text-sm ${
                            message.type === 'success' ? 'text-green-800' : 'text-red-800'
                        }`}>
                            {message.text}
                        </p>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SendFriendRequest;