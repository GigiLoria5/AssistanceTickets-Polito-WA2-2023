import {Row, Col, Button, Container,} from "react-bootstrap";
import SearchProfile from "./SearchProfile";
import DisplayProfile from "./DisplayProfile";
import FormProfile from "./FormProfile";
import {useState} from "react";
import ProfileAPI from "../ProfileAPI";

function Profiles(props) {
    const [profile,setProfile] = useState({});
    const [isFormVisible,setIsFormVisible] = useState(false);

    function getProfileByEmail(email) {
        ProfileAPI.getProfileByEmail(email)
            .then((profile) => setProfile(profile))
            .catch(err=>console.log(err))
    }
    const changeVisible = () => {
        setIsFormVisible(isFormVisible=>!isFormVisible)
    }

    return (
        <>
        {
            isFormVisible ?
                <FormProfile changeVisible={changeVisible}/>
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
