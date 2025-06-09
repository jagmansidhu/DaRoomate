import {useAuth0} from "@auth0/auth0-react";
import {useNavigate} from "react-router-dom";
import React, {useState} from "react";

const Personal = () => {
    const {user, isLoading, getAccessTokenSilently} = useAuth0();
    const navigate = useNavigate();
    const [phone, setPhone] = useState(user?.user_metadata?.phone || '');
    const [firstName, setFirstName] = useState(user?.user_metadata?.given || '');
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
            const accessToken = await getAccessTokenSilently({
                audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                scope: "update:profile",
            });

            const response = await fetch('http://localhost:8085/api/additional_info', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${accessToken}`,
                },
                body: JSON.stringify({
                    firstName,
                    lastName,
                    phone,
                }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to update profile.');
            }

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

    return (
        <div>
            <h1>Complete Your Profile</h1>
            <p>Please provide a few more details to continue.</p>

            {error && <p style={{color: 'red'}}>{error}</p>}
            {success && <p style={{color: 'green'}}>Profile updated successfully!</p>}

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
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <button type="submit" disabled={loading}>
                        {loading ? 'Saving...' : 'Complete Profile'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default Personal;