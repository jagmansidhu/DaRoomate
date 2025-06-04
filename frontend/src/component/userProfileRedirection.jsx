import { useEffect, useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios'; // Import axios

const CUSTOM_CLAIM_NAMESPACE = 'https://daroomate.org/';

const useProfileCompletionRedirect = () => {
    const { user, isAuthenticated, isLoading, getAccessTokenSilently } = useAuth0();
    const navigate = useNavigate();
    const [apiCallMade, setApiCallMade] = useState(false);

    useEffect(() => {
        const checkProfileAndProvision = async () => {
            if (isLoading || !isAuthenticated || apiCallMade) {
                return;
            }

            try {
                const accessToken = await getAccessTokenSilently({
                    authorizationParams: {
                        audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                        scope: 'read:data',
                    },
                });

                await axios.get('http://localhost:8085/api/secret_resource', {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                });
                console.log("User provisioned/found in backend via /api/secret_resource.");
                setApiCallMade(true);
            } catch (provisioningError) {
                console.error("Error provisioning user in backend via /api/secret_resource:", provisioningError);
                setApiCallMade(true);
            }
            const isProfileComplete = user?.[`${CUSTOM_CLAIM_NAMESPACE}isProfileComplete`];

            if (isProfileComplete === false) {
                console.log("Profile is incomplete. Redirecting...");
                navigate('/complete-profile');
            } else if (isProfileComplete === true) {
                console.log("Profile is complete. Staying on current page or redirecting to dashboard.");
                if (window.location.pathname === '/complete-profile') {
                    navigate('/dashboard');
                }
            }
        };

        checkProfileAndProvision();
    }, [isAuthenticated, isLoading, user, navigate, getAccessTokenSilently, apiCallMade]);
};

export default useProfileCompletionRedirect;