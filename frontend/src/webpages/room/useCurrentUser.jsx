import { useState, useEffect } from 'react';
import axios from 'axios';

const useCurrentUser = () => {
  const [currentUser, setCurrentUser] = useState(null);
  const [loadingUser, setLoadingUser] = useState(true);
  const [errorUser, setErrorUser] = useState(null);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await axios.get(`${process.env.REACT_APP_BASE_API_URL}/api/get-user`, {
          withCredentials: true,
        });
        setCurrentUser(res.data);
      } catch (err) {
        setErrorUser(err);
      } finally {
        setLoadingUser(false);
      }
    };

    fetchUser();
  }, []);

  return { currentUser, loadingUser, errorUser };
};

export default useCurrentUser;
