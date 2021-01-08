import { fetcher, handleHttpErrors, makeOptions } from "../utils/fetchUtils";
import { jokeURL as url } from "../utils/settings";

function jokeFetcher() {
  const fetchData = (action, setError) => {
    const options = makeOptions("GET", true);

    return fetcher(url, options, action, setError);
  };
  return { fetchData };
}
const facade = jokeFetcher();
export default facade;
