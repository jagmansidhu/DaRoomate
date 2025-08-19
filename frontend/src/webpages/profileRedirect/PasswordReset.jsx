import React, {useEffect, useState} from 'react';
import axios from 'axios';
import {useNavigate} from 'react-router-dom';

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
    }, [navigate]);

    const handlePasswordReset = async (e) => {
        e.preventDefault();

        if (!password) {
            alert('Please enter a new password.');
            return;
        }

        try {
            const response = await axios.put(
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
            <form className="profile-form" onSubmit={handlePasswordReset}>
                <div className="Label">
                    <label>
                        Email:
                        <input
                            type="email"
                            value={email}
                            placeholder={data?.email || 'Enter your email'}
                            onChange={e => setEmail(e.target.value)}
                        />
                    </label>
                </div>

                <br/>
                <div className="Label">
                    <label>
                        Password:
                        <input
                            type="password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                        />
                    </label>
                </div>
                <br/>


                <button type="submit">Update Profile</button>
            </form>
        </div>
    );
};

export default PasswordReset;