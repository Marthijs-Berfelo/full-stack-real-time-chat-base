import i18n from 'i18next';
import I18NextHttpBackend from 'i18next-http-backend';
import I18nextBrowserLanguageDetector from 'i18next-browser-languagedetector';
import { initReactI18next } from 'react-i18next';

i18n
.use(I18NextHttpBackend)
.use(I18nextBrowserLanguageDetector)
.use(initReactI18next)
.init({
  defaultNS: 'common',
  fallbackLng: 'en',
  saveMissing: true,
  interpolation: {
    escapeValue: false
  }
});

export default i18n;