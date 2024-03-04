import React from 'react';
import { Button, Form, Input, Modal, Typography } from 'antd';
import { useTranslation } from 'react-i18next';
import { useRegistration } from '../registration-modal/useRegistration';
import { RegistrationForm } from '../registration-modal/RegistrationForm';
import { useLogin } from './useLogin';

const { Link } = Typography;

export function LoginForm() {
  const { t } = useTranslation(['common']);
  const { loginForm, onSubmit } = useLogin();
  const {
    registrationForm,
    registrationLoading,
    onOpenModal,
    showModal,
    onSubmitRegistration,
    onOk,
    onCancel,
  } = useRegistration();
  return (
    <Form
      onFinish={onSubmit}
      form={loginForm}
      labelAlign="left"
      autoComplete="off"
      layout="vertical"
    >
      <Form.Item name="username" label={t('common:email')} rules={[{ required: true }]}>
        <Input />
      </Form.Item>
      <Form.Item name="password" label={t('common:password')} rules={[{ required: true }]}>
        <Input.Password autoComplete="on" />
      </Form.Item>
      <div className="flex flex-col justify-center">
        <Button className="my-2" type="primary" htmlType="submit">
          {t('common:login')}
        </Button>
        <Link className="self-center" onClick={onOpenModal}>
          {t('common:register')}
        </Link>
      </div>
      <Modal
        title={t('common:register')}
        onOk={onOk}
        onCancel={onCancel}
        open={showModal}
        confirmLoading={registrationLoading}
      >
        <RegistrationForm form={registrationForm} onSubmitRegistration={onSubmitRegistration} />
      </Modal>
    </Form>
  );
}
