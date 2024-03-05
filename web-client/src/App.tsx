import React, { Suspense } from 'react';
import './styles/global.scss';
import { ConfigProvider } from 'antd';
import { andTheme } from './styles/antd.theme';
import { AuthProvider } from './components/AuthContext';
import { I18nextProvider } from 'react-i18next';
import i18n from './i18n';
import Loader from './components/Loader';
import { PageLayout } from './components/layout/PageLayout';

function App() {
  return (
    <ConfigProvider theme={andTheme}>
      <Suspense fallback={<Loader />}>
        <AuthProvider>
          <I18nextProvider i18n={i18n} defaultNS={'common'}>
            <PageLayout />
          </I18nextProvider>
        </AuthProvider>
      </Suspense>
    </ConfigProvider>
  );
}

export default App;
