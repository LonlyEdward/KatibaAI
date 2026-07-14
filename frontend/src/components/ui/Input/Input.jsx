import styles from "./Input.module.css";

function Input({
                   label,
                   type = "text",
                   placeholder = "",
                   value,
                   onChange,
                   error,
                   disabled = false,
                   required = false,
                   fullWidth = true,
                   ...props
               }) {
    return (
        <div
            className={`${styles.container} ${
                fullWidth ? styles.fullWidth : ""
            }`}
        >
            {label && (
                <label className={styles.label}>
                    {label}

                    {required && (
                        <span className={styles.required}>
                            *
                        </span>
                    )}
                </label>
            )}

            <input
                className={`${styles.input} ${
                    error ? styles.errorInput : ""
                }`}
                type={type}
                placeholder={placeholder}
                value={value}
                onChange={onChange}
                disabled={disabled}
                {...props}
            />

            {error && (
                <p className={styles.error}>
                    {error}
                </p>
            )}
        </div>
    );
}

export default Input;