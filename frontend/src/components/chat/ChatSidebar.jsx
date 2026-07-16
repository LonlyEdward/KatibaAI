import useChat from "../../contexts/chat/useChat";

import styles from "./ChatSidebar.module.css";


function ChatSidebar(){


    const {
        sessions,
        loadSession
    } = useChat();



    return (

        <aside className={styles.sidebar}>


            <h3>
                History
            </h3>



            {
                sessions.map(session=>(


                    <button

                        key={session.id}

                        className={styles.session}

                        onClick={()=>
                            loadSession(session.id)
                        }

                    >

                        {session.title}


                    </button>


                ))
            }


        </aside>

    );

}


export default ChatSidebar;