import {Row, Col, Button, Container, Alert,} from "react-bootstrap";
import SearchProfile from "./SearchProfile";
import DisplayProfile from "./DisplayProfile";
import FormProfile from "./FormProfile";
import React, {useState} from "react";
import API from "../API";

const ErrorPopup = ({error,resetError}) => {
    return (
        <Row className="mt-3">
            <Col>
                <Alert variant={error==="Success" ? "success" : "danger"} dismissible onClose={resetError} className="roundedError">
                    <Alert.Heading>{error}</Alert.Heading>
                </Alert>
            </Col>
        </Row>
    )
}
function Profiles(props) {
    const [editMode,setEditMode] = useState(false);
    const [profile,setProfile] = useState({});
    const [isFormVisible,setIsFormVisible] = useState(false);
    const [error,setError]= useState('');

    function getProfileByEmail(email) {
        API.getProfileByEmail(email)
            .then((prof) => {
                setProfile(prof);
                setError('');
            })
            .catch(err=>setError(err.error))
    }
    function addProfile(newProfile){
        setProfile({});
        API.addProfile(newProfile)
            .then(()=>setError('Success'))
            .catch(err=>setError(err.error))
    }

    function updateProfile(newProfile,email){
        setProfile({});
        API.updateProfile(newProfile,email)
            .then(()=>setError('Success'))
            .catch(err=>setError(err.error))
    }
    const changeVisible = () => {
        setIsFormVisible(isFormVisible=>!isFormVisible)
    }
    const changeEditMode = () =>{
        setEditMode(editMode => !editMode);
    }
    const resetError = () => {
        setError('');
    }



    return (
        <>
        {
            isFormVisible ?
                <>
                    {editMode ?
                        <FormProfile changeVisible={changeVisible} addProfile={updateProfile} profile={profile} changeEditMode={changeEditMode}/>
                        :
                        <FormProfile changeVisible={changeVisible} addProfile={addProfile}/>
                    }
                </>
                :
                <Container style={{maxWidth: "75%"}}>
                    {error ? <ErrorPopup error={error} resetError={resetError}/> : null}
                    <Row>
                        <Col>
                            <SearchProfile getProfileByEmail={getProfileByEmail}></SearchProfile>
                        </Col>
                        <Col>
                            {'id' in profile && <DisplayProfile profile={profile} changeEditMode={changeEditMode} changeVisible={changeVisible}/>}
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
