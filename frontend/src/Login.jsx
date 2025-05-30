import React, { useEffect } from 'react';
import { useAuth0 } from '@auth0/auth0-react';

const Login = () => {
    const { loginWithRedirect } = useAuth0();

    useEffect(() => {
        loginWithRedirect();
    }, [loginWithRedirect]);

    return (
        <div>
            <h1>Redirecting to Login...</h1>
            <p>You are being redirected to the authentication page.</p>
        </div>
    );
};

export default Login;