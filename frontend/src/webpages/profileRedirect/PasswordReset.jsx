import React, {useEffect, useState} from 'react';
import axios from 'axios';
import {useNavigate} from 'react-router-dom';

const PasswordReset = () => {
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [apiLoading, setApiLoading] = useState(true);
    const [password, setPassword] = useState('');
    const [curPassword, setCurPassword] = useState('');
    const [email, setEmail] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

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
            setError("Please enter a new password.");
            return;
        }

        try {
            const response = await axios.put(
                `${process.env.REACT_APP_BASE_API_URL}/user/updateProfile`,
                {
                    email: data.email,
                    curPassword: curPassword,
                    password: password
                },
                {
                    withCredentials: true,
                }
            );

            if (response.status === 200) {
                setSuccessMessage("Password reset successful ✅");

                // redirect after 2 seconds
                setTimeout(() => {
                    navigate('/profile');
                }, 2000);
            } else {
                setError("Password reset failed.");
            }
        } catch (err) {
            console.error('Error resetting password:', err);
            setError("An error occurred while resetting the password.");
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
                        Current Password:
                        <input
                            type="password"
                            value={curPassword}
                            onChange={e => setCurPassword(e.target.value)}
                        />
                    </label>
                </div>

                <br/>
                <div className="Label">
                    <label>
                        New Password:
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

            {successMessage && (
                <div style={{
                    position: "fixed",
                    bottom: "20px",
                    right: "20px",
                    background: "#4CAF50",
                    color: "white",
                    padding: "12px 20px",
                    borderRadius: "6px",
                    boxShadow: "0px 2px 6px rgba(0,0,0,0.2)",
                    fontSize: "14px"
                }}>
                    {successMessage}
                </div>
            )}
        </div>
    );
};

export default PasswordReset;
