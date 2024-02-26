import React, { createContext, PropsWithChildren, useContext, useState } from 'react';


export function useLayout(): LayoutContextType {
  const context = useContext(LayoutContext);
  if (context) {
    return context;
  }
  throw Error('`useLayout` must be used with the `LayoutProvider`');

}

export function LayoutProvider({ children }: PropsWithChildren): JSX.Element {
  const [showUserMenu, setShowUserMenu] = useState(false);

  function toggleUserMenu(): void {
    setShowUserMenu(previous => !previous);
  }

  const context = {
    showUserMenu,
    toggleUserMenu,
  };

  return <LayoutContext.Provider value={context}>{children}</LayoutContext.Provider>
}

interface LayoutContextType {
  toggleUserMenu: () => void;
  showUserMenu: boolean;
}

const LayoutContext = createContext<LayoutContextType | undefined>(undefined);