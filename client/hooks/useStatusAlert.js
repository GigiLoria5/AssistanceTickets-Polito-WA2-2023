import {useState} from 'react';
import {Alert} from 'react-bootstrap';
import {AlertType} from "../enums/AlertType";

export const useStatusAlert = () => {
    const [message, setMessage] = useState(null);

    const resetStatusAlert = () => {
        setMessage(null);
    };

    const StatusAlertComponent = () => {
        return (
            message && (
                <Alert
                    variant={message.type === AlertType.SUCCESS ? 'success' : 'danger'}
                    dismissible
                    onClose={resetStatusAlert}
                    className="roundedError"
                >
                    <Alert.Heading>{message.type}</Alert.Heading>
                    <p>{message.text}</p>
                </Alert>
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
