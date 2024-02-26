import { Form, FormInstance, message, Modal } from 'antd';
import { AccountRegistration } from '../../../api/account/models';
import { useState } from 'react';
import { useAuth } from '../../AuthContext';
import { useTranslation } from 'react-i18next';
const { useForm } = Form
export function useRegistration(): RegistrationHook {
  const { t } = useTranslation(['common', 'user'])
  const { registerAccount } = useAuth();
  const [registrationForm] = useForm<AccountRegistration>();
  const [registrationLoading, setRegistrationLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);

  function onOpenModal() {
    registrationForm.setFieldsValue({});
    setShowModal(true);
  }

  async function onSubmitRegistration(registration: AccountRegistration): Promise<void> {
    setRegistrationLoading(true)
    await registerAccount(registration)
      .then(confirmRegistration)
      .catch(rejectRegistration)
      .finally(() => setRegistrationLoading(false))
  }

  function onOk() {
    registrationForm.submit()
  }

  function onCancel() {
    registrationForm.resetFields();
    setShowModal(false);
  }

  function confirmRegistration() {
    setShowModal(false);
    Modal.info({
      title: t('common:register'),
      content: t('user:registration-success')
    });
  }

  async function rejectRegistration() {
    await message.error(t('user:errors.account-registration'))
  }

  return {
    registrationForm,
    showModal,
    registrationLoading,
    onOpenModal,
    onSubmitRegistration,
    onOk,
    onCancel
  }
}

interface RegistrationHook {
  registrationForm: FormInstance<AccountRegistration>;
  showModal: boolean;
  registrationLoading: boolean;
  onOpenModal: () => void;
  onSubmitRegistration: (registration: AccountRegistration) => Promise<void>;
  onOk: () => void;
  onCancel: () => void;
}