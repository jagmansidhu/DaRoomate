import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const PasswordReset = () => {
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [apiLoading, setApiLoading] = useState(true);
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');

    const navigate = useNavigate();

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
    }, [navigate]); // navigate is a stable reference, so it's a good dependency

    const handlePasswordReset = async (e) => {
        e.preventDefault(); // Prevents the default form submission behavior

        if (!password) {
            alert('Please enter a new password.');
            return;
        }

        try {
            const response = await axios.post(
                `${process.env.REACT_APP_BASE_API_URL}/user/updateProfile`,
                {
                    email: data.email,
                    password: password
                },
                {
                    withCredentials: true,
                }
            );

            if (response.status === 200) {
                alert('Password reset successful');
                navigate('/profile'); // Redirect to profile page
            } else {
                alert('Password reset failed');
            }
        } catch (err) {
            console.error('Error resetting password:', err);
            alert('An error occurred while resetting the password');
        }
    };

    return (
        <div className="container">
            <form onSubmit={handlePasswordReset}>
                <label>
                    Password:
                    <input
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                    />
                </label>
                <br />
                <label>
                    Email:
                    <input
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required
                    />
                </label>
                <br />

                <button type="submit">Update Profile</button>
            </form>
        </div>
    );
};

export default PasswordReset;