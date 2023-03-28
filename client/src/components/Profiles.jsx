import {Row, Col, Button, Container,} from "react-bootstrap";
import SearchProfile from "./SearchProfile";
import DisplayProfile from "./DisplayProfile";
import FormProfile from "./FormProfile";
import {useState} from "react";
import API from "../API";

function Profiles(props) {
    const [profile,setProfile] = useState({});
    const [isFormVisible,setIsFormVisible] = useState(false);
    const [error,setError]= useState('');

    function getProfileByEmail(email) {
        API.getProfileByEmail(email)
            .then((profile) => setProfile(profile))
            .catch(err=>setError(err.error))
    }
    function addProfile(newProfile){
        API.addProfile(newProfile)
            .then(()=>setError(''))
            .catch(err=>setError(err.error))
    }
    const changeVisible = () => {
        setIsFormVisible(isFormVisible=>!isFormVisible)
    }

    return (
        <>
        {
            isFormVisible ?
                <FormProfile changeVisible={changeVisible} addProfile={addProfile}/>
                :
                <Container style={{maxWidth: "75%"}}>
                    <Row>
                        <Col>
                            <SearchProfile getProfileByEmail={getProfileByEmail}></SearchProfile>
                        </Col>
                        <Col>
                            {'id' in profile && <DisplayProfile profile={profile}/>}
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
