import React from 'react';
import { useChatRegistration } from './useChatRegistration';
import { Button, Flex, Form, Input } from 'antd';
import { useTranslation } from 'react-i18next';

export default function LoginPage() {
  const { t } = useTranslation(['common', 'user'])
  const { loading, registrationForm, onSubmit, onReset } = useChatRegistration();

  return (
    <Flex justify={'center'}>
      <Form
        form={registrationForm}
        onFinish={onSubmit}
        onReset={onReset}
        layout="vertical"
      >
        <Form.Item
          name="nickName"
          label={t('user:nick-name')}
          rules={[
            { required: true }
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          name="statusMessage"
          label={t('user:status')}
        >
          <Input.TextArea />
        </Form.Item>
        <Button htmlType="submit" type="primary" loading={loading}>
          {t('common:login')}
        </Button>
      </Form>
    </Flex>
  )
}