import React, { useEffect, useState } from 'react';
import api from './Api';

const Dashboard = () => {
    const [data, setData] = useState(null);

    useEffect(() => {
        api.get('/secret_resource')
            .then((response) => {
                setData(response.data);
            })
            .catch((error) => {
                console.error('Error fetching protected resource:', error);
            });
    }, []);

    return (

        <div>
            <h1>Daa</h1>
            <p>Loading data...</p>
            <pre>{JSON.stringify(data, null, 2)}</pre>
        </div>


    );
};

export default Dashboard;
