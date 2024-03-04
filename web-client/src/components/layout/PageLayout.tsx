import React from 'react';
import { Layout } from 'antd';
import { LayoutProvider } from './LayoutContext';
import { PageHeader } from './PageHeader';
import { PageContent } from './PageContent';
import { UserMenu } from './user-menu/UserMenu';

export function PageLayout() {
  return (
    <Layout className="h-screen min-w-[768px] overflow-x-auto overflow-y-clip">
      <LayoutProvider>
        <Layout>
          <PageHeader />
          <PageContent />
        </Layout>
        <UserMenu />
      </LayoutProvider>
    </Layout>
  );
}
