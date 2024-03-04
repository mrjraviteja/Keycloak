import { useState, useEffect } from 'react'
import './App.css'
import { httpClient } from './HttpClient'
import Keycloak from 'keycloak-js'
import reactLogo from './assets/react.svg'
import keycloakLogo from './assets/keycloak.png'

  const tenantRealm = 'TestJWT'
  console.log(tenantRealm)

  const clientId = `test-jwt`
  console.log(clientId)

  const initOptions = {
    url: 'http://localhost:8080/',
    realm: tenantRealm,
    clientId: clientId
  }

  const kc = new Keycloak(initOptions)

  kc.init({
    onLoad: 'login-required',
    checkLoginIframe: true,
    pkceMethod: 'S256'
  }).then((auth) => {
    if(!auth){
      console.log('Authentication failed. Reloading...');
    }
    else
    {
      console.info('Authenticated');
      console.log('auth',auth)
      console.log('Keycloak',kc)
      console.log('Access Token',kc.token)

      httpClient.defaults.headers.common['Authorization'] = `Bearer ${kc.token}`

      kc.onTokenExpired = () => {
        console.log('Token expired')
      }
    }
  }, () => {
    console.error('Authentication Failed')
  })

function App() {
  
  const [infoMessage, setInfoMessage] = useState('')


  return (
    <>
      <div>
        <a href="https://react.dev" target="_blank">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
        <a href="https://keycloak.org" target="_blank">
          <img src={keycloakLogo} className="logo react" alt="Keycloak logo" />
        </a>
      </div>
      <h1>React + Keycloak App</h1>
      <div className="card">
        <p style={{ wordBreak: 'break-all'}} id='infoPanel'>
          {infoMessage}
        </p>
      </div>
      <div className='grid'>
        <button onClick={() => {setInfoMessage(kc.authenticated ? 'Authenticated: TRUE' : 'Authenticated: FALSE')}} className='btn'>isAuthenticated?</button>
        <button onClick={() => {kc.login()}} className='btn'>Login</button>
        <button onClick={() => {setInfoMessage(kc.token)}} className='btn'>Show Access Token</button>
        <button onClick={() => {setInfoMessage(JSON.stringify(kc.tokenParsed))}} className='btn'>Show Parsed Token</button>
        <button onClick={() => {setInfoMessage(kc.isTokenExpired(5).toString())}} className='btn'>Check Token expired?</button>
        <button onClick={() => {kc.updateToken(10).then((refreshed) => { setInfoMessage('Token Refreshed: '+refreshed.toString())}, (e) => { setInfoMessage('Refresh Error')})}} className='btn'>Update Token</button>
        <button onClick={() => {kc.logout({redirectUri: 'http://localhost:5173/'})}} className='btn'>Logout</button>
        <button onClick={() => {setInfoMessage(kc.hasRealmRole('realm_user').toString())}} className='btn'>has User role?</button>
        <button onClick={() => {setInfoMessage(kc.tokenParsed.name)}} className='btn'>Show user name</button>
      </div>
      <div>Current Realm: {kc.realm} / Client: {kc.clientId} / Flow: {kc.flow}</div>
      <p className="read-the-docs">
        Click on the React and Keycloak logos to learn more
      </p>
    </>
  )
}

export default App
