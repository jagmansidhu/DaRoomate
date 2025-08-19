import {useNavigate} from "react-router-dom";
import React, {useEffect, useState} from "react";
import axios from "axios";

const Personal = () => {
    const navigate = useNavigate();
    const [data, setData] = useState(null);
    const [apiLoading, setApiLoading] = useState(true);
    const [phone, setPhone] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');

    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    useEffect(() => {
        const checkProfileAndFetchData = async () => {
            try {
                const response = await axios.get(`${process.env.REACT_APP_BASE_API_URL}/api/get-user`, {
                    withCredentials: true,
                });
                setData(response.data);
            } catch (err) {
                console.error('Error fetching protected resource:', err);
                setError(err);
            } finally {
                setApiLoading(false);
            }
        };

        checkProfileAndFetchData();
    }, [navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setApiLoading(true);
        setError(null);
        setSuccess(false);

        try {
            const response = await axios.put(`${process.env.REACT_APP_BASE_API_URL}/api/profile/update_profile`, {
                firstName,
                lastName,
                phone
            }, {
                withCredentials: true, headers: {'Content-Type': 'application/json'},
            });

            setSuccess(true);
            navigate('/profile');

        } catch (err) {
            console.error('Error updating profile:', err);
            setError(err.response?.data?.message || 'Failed to update profile. Please try again.');
        } finally {
            setApiLoading(false);
        }
    };

    return (<div>
            <h1>Update Information</h1>
            {error && <p style={{color: 'red'}}>{error}</p>}
            {success && <p style={{color: 'green'}}>Profile updated successfully!</p>}

            <form className="profile-form" onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="firstName">First Name:</label>
                    <input
                        type="text"
                        id="firstName"
                        value={firstName}
                        placeholder={data?.firstName || 'Enter your first name'}
                        onChange={(e) => setFirstName(e.target.value)}
                    />
                </div>
                <br/>
                <div>
                    <label htmlFor="lastName">Last Name:</label>
                    <input
                        type="text"
                        id="lastName"
                        value={lastName}
                        placeholder={data?.lastName || 'Enter your first name'}
                        onChange={(e) => setLastName(e.target.value)}
                    />
                </div>
                <br/>
                <div>
                    <label htmlFor="phoneNumber">Phone Number:</label>
                    <input
                        type="text"
                        id="phoneNumber"
                        value={phone}
                        placeholder={data?.phone || 'xxx-xxx-xxxx'}
                        onChange={(e) => setPhone(e.target.value)}
                    />
                </div>
                <div>
                    <button type="submit" disabled={apiLoading}>
                        {apiLoading ? 'Saving...' : 'Complete'}
                    </button>
                </div>
            </form>
        </div>);
};

export default Personal;
