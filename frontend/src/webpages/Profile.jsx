import React, {useEffect, useState} from 'react';
import axios from 'axios';
import {useNavigate} from 'react-router-dom';

const Profile = () => {
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [apiLoading, setApiLoading] = useState(true);
    const [accessToken, setAccessToken] = useState(null);

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
    }, [navigate, setAccessToken]);



    return (
        <div className="profile">
            <h1>Profile</h1>
            <h2>Login Info</h2>
            <p>Email : {data?.email}</p>
            <p>Password : ********</p>
            <button onClick={() => navigate('/reset-password')}>Change Login</button>
            <h2>Personal Info</h2>
            <p>FirstName : {data?.firstName || 'na'}</p>
            <p>LastName : {data?.lastName || ' '}</p>
            <p>Phone : {data?.phone || ' '}</p>
            <button onClick={() => navigate('/update-personal')}>Change Personal</button>

        </div>
    );
};

export default Profile;