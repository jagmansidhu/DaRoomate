import React, { useEffect, useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { useNavigate, useLocation } from 'react-router-dom';

const VerificationPopup = ({ onPopupVisibilityChange }) => {
    const { error: auth0SDKError, logout } = useAuth0();
    const [showErrorPopup, setShowErrorPopup] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();
    const location = useLocation();

    const [hasHandledError, setHasHandledError] = useState(false);

    useEffect(() => {
        if (hasHandledError) {
            return;
        }

        const queryParams = new URLSearchParams(location.search);
        const urlError = queryParams.get('error');
        const urlErrorDescription = queryParams.get('error_description');

        let errorToDisplay = null;

        if (urlError === 'access_denied' && urlErrorDescription) {
            if (urlErrorDescription.includes('Please verify your email')) {
                errorToDisplay = 'Your email address is not verified. Please check your inbox for the verification link to complete your login.';
            } else {
                errorToDisplay = urlErrorDescription || 'Access denied due to an unknown reason.';
            }
        } else if (auth0SDKError) {
            errorToDisplay = auth0SDKError.message || 'An unexpected authentication error occurred.';
        }

        if (errorToDisplay) {
            setErrorMessage(errorToDisplay);
            setShowErrorPopup(true);
            setHasHandledError(true);
            navigate(location.pathname, { replace: true });
        }
    }, [location, auth0SDKError, navigate, hasHandledError]);

    useEffect(() => {
        if (onPopupVisibilityChange) {
            onPopupVisibilityChange(showErrorPopup);
        }
    }, [showErrorPopup, onPopupVisibilityChange]);

    const handleClosePopup = () => {
        setShowErrorPopup(false);
        setErrorMessage('');
        setHasHandledError(false);

        logout({ logoutParams: { returnTo: window.location.origin } });
    };

    if (!showErrorPopup) {
        return null;
    }

    return (
        <div>
            <div >
                <h2>Verification Error</h2>
                <p>{errorMessage}</p>
                <button onClick={handleClosePopup}>Back To Home Page</button>
            </div>
        </div>
    );
};

export default VerificationPopup;