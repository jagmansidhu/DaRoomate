import { useAuth0 } from '@auth0/auth0-react';
import {useEffect} from "react";

const Login = () => {
    const { loginWithRedirect } = useAuth0();

    useEffect(() => {
        loginWithRedirect({
            appState: { returnTo: "/" }
        });
    }, [loginWithRedirect]);

    return null;
};

export default Login;