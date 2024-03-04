import React, { JSX } from 'react';
import { Form, FormInstance, Input } from 'antd';
import { AccountRegistration } from '../../../api/account/models';
import { useTranslation } from 'react-i18next';

interface RegistrationFormProps {
  form: FormInstance<AccountRegistration>;
  onSubmitRegistration: (registration: AccountRegistration) => Promise<void>;
}

export function RegistrationForm({
  form,
  onSubmitRegistration,
}: RegistrationFormProps): JSX.Element {
  const { t } = useTranslation(['common']);
  return (
    <Form form={form} onFinish={onSubmitRegistration} labelAlign="left" layout="vertical">
      <Form.Item className="mb-0" label={t('common:name')}>
        <Form.Item name="firstName" className="inline-block w-4/12" rules={[{ required: true }]}>
          <Input placeholder={t('common:first-name')} />
        </Form.Item>
        <Form.Item name="middleName" className="inline-block w-2/12 mx-4">
          <Input placeholder={t('common:middle-name')} />
        </Form.Item>
        <Form.Item name="lastName" className="inline-block w-5/12" rules={[{ required: true }]}>
          <Input placeholder={t('common:last-name')} />
        </Form.Item>
      </Form.Item>
      <Form.Item name="email" label={t('common:email')} rules={[{ required: true }]}>
        <Input />
      </Form.Item>
      <Form.Item name="password" label={t('common:password')} rules={[{ required: true }]}>
        <Input.Password />
      </Form.Item>
      <Form.Item
        name="passwordConfirmation"
        label={t('common:password-confirm')}
        rules={[
          { required: true },
          ({ getFieldValue }) => ({
            validator(_, value) {
              if (!value || getFieldValue('password') === value) {
                return Promise.resolve();
              }
              return Promise.reject(new Error(t('common:errors.password-confirm')));
            },
          }),
        ]}
      >
        <Input.Password />
      </Form.Item>
    </Form>
  );
}
