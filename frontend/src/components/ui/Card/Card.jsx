import styles from "./Card.module.css";

function Card({
                  children,
                  title,
                  subtitle,
                  className = "",
              }) {

    return (

        <div className={`${styles.card} ${className}`}>

            {(title || subtitle) && (

                <div className={styles.header}>

                    {title && (

                        <h2 className={styles.title}>
                            {title}
                        </h2>

                    )}

                    {subtitle && (

                        <p className={styles.subtitle}>
                            {subtitle}
                        </p>

                    )}

                </div>

            )}

            <div className={styles.content}>

                {children}

            </div>

        </div>

    );

}

export default Card;