import {useEffect, useRef} from 'react';
import {useAuth0} from '@auth0/auth0-react';
import {useLocation, useNavigate} from 'react-router-dom';
import axios from 'axios';

const useProfileCompletionRedirect = () => {
    const {user, isAuthenticated, isLoading, getAccessTokenSilently} = useAuth0();
    const navigate = useNavigate();
    const location = useLocation();
    const hasCheckedProfile = useRef(false);

    useEffect(() => {
        const checkProfileAndRedirect = async () => {
            if (!isAuthenticated || isLoading || hasCheckedProfile.current) {
                return;
            }

            try {
                const accessToken = await getAccessTokenSilently({
                    authorizationParams: {
                        audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                        scope: 'read:data',
                    },
                });

                const response = await axios.get('http://localhost:8085/api/profile-status', {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                });

                const isProfileComplete = response.data.isComplete;
                console.log("Profile completion status from backend:", isProfileComplete);

                if (!isProfileComplete && location.pathname !== '/complete-profile') {
                    navigate('/complete-profile');
                } else if (isProfileComplete && location.pathname === '/complete-profile') {
                    navigate('/dashboard');
                }
                hasCheckedProfile.current = true;

            } catch (error) {
                console.error("Error checking profile status:", error);
            }
        };

        if (isAuthenticated && !isLoading) {
            checkProfileAndRedirect();
        }

    }, [isAuthenticated, isLoading, navigate, getAccessTokenSilently, location.pathname]);
};

export default useProfileCompletionRedirect;