import React, { useEffect, useState } from 'react';
import api from './Api';

const Home = () => {
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
            <h1>Home</h1>
            {data ? (
                <pre>{JSON.stringify(data, null, 2)}</pre>
            ) : (
                <p>Loading data...</p>
            )}
        </div>
    );
};

export default Home;
