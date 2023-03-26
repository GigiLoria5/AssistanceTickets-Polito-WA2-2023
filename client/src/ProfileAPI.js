const APIURL = new URL('http://localhost:8080/API/')
async function getProfileByEmail(email){
    const response = await fetch(new URL('profiles/' + email, APIURL), {credentials:'include'})
    const profile =  await response.json()
    if(response.ok)
        return profile
    else
        throw profile
}

const ProfileAPI = {getProfileByEmail}
export default ProfileAPI