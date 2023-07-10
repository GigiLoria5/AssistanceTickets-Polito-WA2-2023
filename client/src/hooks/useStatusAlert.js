import {useEffect, useRef, useState} from 'react';
import {Alert} from 'react-bootstrap';
import {AlertType} from "../enums/AlertType";

export const useStatusAlert = () => {
    const [message, setMessage] = useState(null);
    const alertRef = useRef(null);

    const resetStatusAlert = () => {
        setMessage(null);
    };

    const StatusAlertComponent = () => {
        useEffect(() => {
            if (alertRef.current) {
                alertRef.current.scrollIntoView({behavior: "smooth"});
            }
        }, [message]);

        return (
            message && (
                <div ref={alertRef}>
                    <Alert
                        variant={message.type === AlertType.SUCCESS ? 'success' : 'danger'}
                        dismissible
                        onClose={resetStatusAlert}
                        className="roundedError"
                    >
                        <Alert.Heading>{message.type}</Alert.Heading>
                        <p>{message.text.charAt(0).toUpperCase() + message.text.slice(1)}</p>
                    </Alert>
                </div>
            )
        );
    };

    return {
        StatusAlertComponent,
        showSuccess: (text) => {
            const type = AlertType.SUCCESS
            setMessage({type, text});
        },
        showError: (text) => {
            const type = AlertType.ERROR
            setMessage({type, text});
        },
        resetStatusAlert
    };
};
