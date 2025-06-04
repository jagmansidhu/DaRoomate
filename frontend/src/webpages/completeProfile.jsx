import React, { useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { useNavigate } from 'react-router-dom';

const CUSTOM_CLAIM_NAMESPACE = 'https://daroomate.org/';

const CompleteProfile = () => {
    const { user, isLoading } = useAuth0();
    const navigate = useNavigate();
    const [phoneNumber, setPhoneNumber] = useState(user?.user_metadata?.phoneNumber || '');
    const [address, setAddress] = useState(user?.user_metadata?.address || '');
    const [firstName, setFirstName] = useState(user?.user_metadata?.firstName || '');
    const [lastName, setLastName] = useState(user?.user_metadata?.lastName || '');

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccess(false);

        try {
            await new Promise(resolve => setTimeout(resolve, 1000));

            setSuccess(true);
            navigate('/profile');

        } catch (err) {
            console.error('Error updating profile:', err);
            setError('Failed to update profile. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    if (isLoading) {
        return <div>Loading profile data...</div>;
    }

    if (!user || !user.sub) {
        navigate('/');
        return null;
    }

    const isProfileCompleteFromToken = user[`${CUSTOM_CLAIM_NAMESPACE}isProfileComplete`];
    if (isProfileCompleteFromToken === true && !success) {
        navigate('/dashboard');
        return null;
    }

    return (
        <div>
            <h1>Complete Your Profile</h1>
            <p>Please provide a few more details to continue.</p>

            {error && <p style={{ color: 'red' }}>{error}</p>}
            {success && <p style={{ color: 'green' }}>Profile updated successfully!</p>}

            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="firstName">First Name:</label>
                    <input
                        type="text"
                        id="firstName"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="lastName">Last Name:</label>
                    <input
                        type="text"
                        id="LastName"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="phoneNumber">Phone Number:</label>
                    <input
                        type="text"
                        id="phoneNumber"
                        value={phoneNumber}
                        onChange={(e) => setPhoneNumber(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="address">Address:</label>
                    <textarea
                        id="address"
                        value={address}
                        onChange={(e) => setAddress(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" disabled={loading}>
                    {loading ? 'Saving...' : 'Complete Profile'}
                </button>
            </form>
        </div>
    );
};

export default CompleteProfile;