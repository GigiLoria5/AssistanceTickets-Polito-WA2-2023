import {Button, Col, Container, Row,} from "react-bootstrap";
import SearchProfile from "./SearchProfile";
import DisplayProfile from "./DisplayProfile";
import FormProfile from "./FormProfile";
import React, {useState} from "react";
import API from "../API";
import {useStatusAlert} from "../../hooks/useStatusAlert";

function Profiles() {
    const [editMode, setEditMode] = useState(false);
    const [profile, setProfile] = useState({});
    const [isFormVisible, setIsFormVisible] = useState(false);
    const {StatusAlertComponent, showSuccess, showError, resetStatusAlert} = useStatusAlert();

    function getProfileByEmail(email) {
        API.getProfileByEmail(email)
            .then((profile) => {
                setProfile(profile);
                resetStatusAlert();
            })
            .catch(err => showError(err.error))
    }

    function addProfile(newProfile) {
        setProfile({});
        API.addProfile(newProfile)
            .then(() => showSuccess("UserProfile added successfully"))
            .catch(err => showError(err.error))
    }

    function updateProfile(newProfile, email) {
        setProfile({});
        API.updateProfile(newProfile, email)
            .then(() => showSuccess("UserProfile updated successfully"))
            .catch(err => showError(err.error))
    }

    const changeVisible = () => {
        setIsFormVisible(isFormVisible => !isFormVisible)
    }

    const changeEditMode = () => {
        setEditMode(editMode => !editMode);
    }

    return (
        <>
            {
                isFormVisible ?
                    <>
                        {editMode ?
                            <FormProfile changeVisible={changeVisible} addProfile={updateProfile} profile={profile}
                                         changeEditMode={changeEditMode}/>
                            :
                            <FormProfile changeVisible={changeVisible} addProfile={addProfile}/>
                        }
                    </>
                    :
                    <Container style={{maxWidth: "75%"}}>
                        <StatusAlertComponent/>
                        <Row>
                            <Col>
                                <SearchProfile getProfileByEmail={getProfileByEmail}></SearchProfile>
                            </Col>
                            <Col>
                                {'id' in profile && <DisplayProfile profile={profile} changeEditMode={changeEditMode}
                                                                    changeVisible={changeVisible}/>}
                            </Col>
                        </Row>

                        <Row className="mt-3 mx-auto" style={{maxWidth: "25%"}}>
                            <Button onClick={changeVisible}>Add new profile</Button>
                        </Row>
                    </Container>

            }
        </>
    );
}

export default Profiles
