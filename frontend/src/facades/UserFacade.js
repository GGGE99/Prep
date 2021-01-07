import { baseURL } from "../utils/settings";
import { makeOptions, handleHttpErrors, fetcher } from "../utils/fetchUtils";

function userFacade() {
  const URL = baseURL + "api/user/";

  const fetchUsers = (action, setError) => {
    const options = makeOptions("GET", true);
    return fetcher(URL + "all", options, action, setError);
  };

  const editRole = (role, username) => {
    const options = makeOptions("POST", true, { role, username });
    return fetch(URL + "edit-role", options).then(handleHttpErrors);
  };

  return {
    fetchUsers,
    editRole,
  };
}

const facade = userFacade();
export default facade;
