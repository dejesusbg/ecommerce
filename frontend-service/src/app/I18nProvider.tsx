'use client';

import React, { ReactNode, useEffect, useState } from 'react';
import { I18nextProvider } from 'react-i18next';
import i18nInstance from './i18n'; // The shared instance
// If using getI18nInstance:
// import { getI18nInstance } from './i18n';
// import { useParams } from 'next/navigation'; // To get locale from URL if needed

interface Props {
  children: ReactNode;
  // locale?: string; // Potentially pass locale if using path-based detection primarily
}

const I18nProviderWrapper: React.FC<Props> = ({ children }) => {
  // const params = useParams();
  // const locale = typeof params?.locale === 'string' ? params.locale : i18nInstance.language;

  // The i18nInstance is already initialized.
  // If you need to change language based on Next.js routing (e.g. /es-CO/mypage),
  // you might do it here.
  // For example, if locale is passed as a prop or derived from `useParams`:
  // useEffect(() => {
  //   if (locale && i18nInstance.language !== locale) {
  //     i18nInstance.changeLanguage(locale);
  //   }
  // }, [locale]);

  // A state to force re-render once i18next is initialized, especially if async ops are involved
  // or language changes.
  const [isInitialized, setIsInitialized] = useState(false);

  useEffect(() => {
    const handleInitialized = () => {
      setIsInitialized(true);
    };

    if (i18nInstance.isInitialized) {
      handleInitialized();
    } else {
      // Listen for initialization (e.g., after backend load)
      i18nInstance.on('initialized', handleInitialized);
    }

    // Listen for language changes to force re-render
    const handleLanguageChanged = (lng: string) => {
      console.log("Language changed to: ", lng);
      setIsInitialized(false); // Trigger re-render by resetting
      setTimeout(() => setIsInitialized(true), 0);
    };
    i18nInstance.on('languageChanged', handleLanguageChanged);

    return () => {
      i18nInstance.off('initialized', handleInitialized);
      i18nInstance.off('languageChanged', handleLanguageChanged);
    };
  }, []);


  // Render children only after i18next is initialized to prevent rendering with default/fallback content
  // or if you handle a loading state.
  if (!isInitialized && !i18nInstance.isInitialized) {
     // You could return a loading spinner here if preferred
    return null;
  }

  return <I18nextProvider i18n={i18nInstance}>{children}</I18nextProvider>;
};

export default I18nProviderWrapper;
