import {useEffect, useState} from "react";
import API from "../../API";
import {handleApiError} from "../../utils/utils";
import {Button, Col, Container, Row, Spinner} from "react-bootstrap";
import EditIcon from "../../assets/icons/EditIcon";
import ClientProfileForm from "../ClientProfileForm";

// Only clientId and showError are needed for use this component outside UserProfile
function ClientProfile({clientId, showSuccess, showError, resetStatusAlert, switchLogoutVisibility}) {
    const [client, setClient] = useState(null);
    const [editMode, setEditMode] = useState(false);
    const [refresh, setRefresh] = useState(false);

    useEffect(() => {
        API.getProfileById(clientId)
            .then(client => {
                console.log(client)
                setClient(client)
            })
            .catch(err => handleApiError(err, showError))
            .finally(() => setRefresh(false))
    }, [refresh])

    const switchEditMode = () => {
        resetStatusAlert()
        setEditMode(editMode => !editMode)
        switchLogoutVisibility()
    }

    const onUpdateSuccess = () => {
        setRefresh(true)
        switchLogoutVisibility()
        setEditMode(false)
        showSuccess("Information updated successfully")
    }

    return (
        editMode
            ? <ClientProfileForm profile={client} onSuccess={onUpdateSuccess} onCancel={switchEditMode}/>
            : <Container className="my-5">
                <Row className="justify-content-center">
                    <Col md={8} className="text-center">
                        <h2 className='text-center'>Client Profile &nbsp;
                            {switchLogoutVisibility &&
                                <Button variant="primary" onClick={switchEditMode}><EditIcon/></Button>}
                        </h2>
                        {
                            !client || refresh
                                ? <Spinner animation="border" variant="primary"/>
                                : (
                                    <div className="p-3">
                                        <p className="mb-2"><b>Name:</b> {client.name} {client.surname}</p>
                                        <p className="mb-2"><b>Email:</b> {client.email}</p>
                                        <p className="mb-2"><b>Phone Number:</b> {client.phoneNumber}</p>
                                        <p>
                                            <b>Location:</b> {client.address}, {client.city}, {client.country}
                                        </p>
                                    </div>
                                )
                        }
                    </Col>
                </Row>
            </Container>
    );
}

export default ClientProfile;
