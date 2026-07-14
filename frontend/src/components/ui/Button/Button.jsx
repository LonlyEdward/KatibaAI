import styles from "./Button.module.css";

function Button({
                    children,
                    variant = "primary",
                    type = "button",
                    disabled = false,
                    loading = false,
                    fullWidth = false,
                    leftIcon,
                    rightIcon,
                    onClick,
                }) {

    const classNames = [
        styles.button,
        styles[variant],
        fullWidth ? styles.fullWidth : "",
    ].join(" ");

    return (
        <button
            type={type}
            className={classNames}
            disabled={disabled || loading}
            onClick={onClick}
        >

            {loading ? (
                <span>Loading...</span>
            ) : (
                <>
                    {leftIcon && (
                        <span className={styles.icon}>
                            {leftIcon}
                        </span>
                    )}

                    <span>{children}</span>

                    {rightIcon && (
                        <span className={styles.icon}>
                            {rightIcon}
                        </span>
                    )}
                </>
            )}

        </button>
    );
}

export default Button;