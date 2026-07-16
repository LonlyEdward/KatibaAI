import { createContext, useEffect, useMemo, useState } from "react";

import authService from "../../services/auth/authService";
import { authManager } from "../../auth/authManager";


export const AuthContext = createContext(null);


function AuthProvider({ children }) {

    const [user, setUser] = useState(null);

    const [accessToken, setAccessToken] = useState(null);

    const [loading, setLoading] = useState(true);



    const login = (response) => {

        authManager.setAccessToken(response.token);

        authManager.setRefreshToken(response.refreshToken);


        setAccessToken(response.token);


        setUser({

            username: response.username,

            email: response.email,

            role: response.role,

        });

    };



    const logout = async () => {

        try {

            const refreshToken = authManager.getRefreshToken();


            if (refreshToken) {

                await authService.logout({

                    refreshToken,

                });

            }


        } finally {

            authManager.clearAccessToken();

            setAccessToken(null);

            setUser(null);

        }

    };



    const refreshAccessToken = async () => {

        const refreshToken = authManager.getRefreshToken();


        // No refresh token means user is not logged in
        if (!refreshToken) {

            return;

        }



        try {

            const response = await authService.refresh({

                refreshToken,

            });


            login(response);


        } catch(error) {


            authManager.clearAccessToken();

            setAccessToken(null);

            setUser(null);


            throw error;

        }

    };



    useEffect(() => {

        async function initialize() {

            try {

                await refreshAccessToken();


            } catch {

                // user is not authenticated

            } finally {

                setLoading(false);

            }

        }


        initialize();


    }, []);



    const value = useMemo(() => ({

        user,

        accessToken,

        loading,


        isAuthenticated: !!accessToken,


        login,

        logout,

        refreshAccessToken,


    }), [

        user,

        accessToken,

        loading

    ]);



    return (

        <AuthContext.Provider value={value}>

            {children}

        </AuthContext.Provider>

    );

}


export default AuthProvider;