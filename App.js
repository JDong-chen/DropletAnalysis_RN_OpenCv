import React,{ Component } from 'react'
import {View} from 'react-native'

import Home from './views/Home'

import { TeaNavigator } from 'teaset'

class App extends Component {


  render() {
    return (
    <TeaNavigator rootView={<Home/>} />
    );
  }

}

export default App