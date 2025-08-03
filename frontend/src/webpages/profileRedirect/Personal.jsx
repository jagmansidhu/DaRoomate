import {useNavigate} from "react-router-dom";
import React, {useState} from "react";
import axios from "axios";

const Personal = () => {
    const navigate = useNavigate();
    const [phone, setPhone] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccess(false);

        try {
            const response = await axios.put(
                `${process.env.REACT_APP_BASE_API_URL}/api/profile/update_profile`,
                { firstName, lastName, phone },
                {
                    withCredentials: true,
                    headers: { 'Content-Type': 'application/json' },
                }
            );

            setSuccess(true);
            navigate('/profile');

        } catch (err) {
            console.error('Error updating profile:', err);
            setError(err.response?.data?.message || 'Failed to update profile. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h1>Change Your Info</h1>
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
                    />
                </div>
                <div>
                    <label htmlFor="lastName">Last Name:</label>
                    <input
                        type="text"
                        id="lastName"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                    />
                </div>
                <div>
                    <label htmlFor="phoneNumber">Phone Number:</label>
                    <input
                        type="text"
                        id="phoneNumber"
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                    />
                </div>
                <div>
                    <button type="submit" disabled={loading}>
                        {loading ? 'Saving...' : 'Complete'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default Personal;
