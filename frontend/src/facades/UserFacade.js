import { baseURL } from "../utils/settings";
import { makeOptions, handleHttpErrors, fetcher } from "../utils/fetchUtils";

function userFacade() {
  const URL = baseURL + "api/user/";

  const fetchUsers = (action, setError) => {
    const options = makeOptions("GET", true);
    return fetcher(URL + "all", options, action, setError);
  };

  const editRole = (role, username, action, setError) => {
    const options = makeOptions("POST", true, { role, username });
    return fetcher(URL + "edit-role", options, action, setError);
  };

  const deleteUser = (username, action, setError) => {
    const options = makeOptions("POST", true, { username });
    return fetcher(URL + "delete", options, action, setError);
  };

  return {
    fetchUsers,
    editRole,
    deleteUser
  };
}

const facade = userFacade();
export default facade;
