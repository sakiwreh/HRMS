import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { Provider } from 'react-redux'
import { store } from './store/store'
import Providers from './app/providers.tsx'
import { RouterProvider } from 'react-router-dom'
import {router} from './app/router'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
     <Provider store={store}>
      <Providers>
        <RouterProvider router={router}/>
      </Providers>
     </Provider>
  </StrictMode>,
)
