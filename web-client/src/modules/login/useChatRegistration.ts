import { Form, FormInstance } from 'antd';
import { ChatRegistration } from '../../api/user/models';
import { useState } from 'react';
const { useForm } = Form

export function useChatRegistration(): ChatRegistrationHook {
  const [loading, setLoading] = useState(false);
  const [registrationForm] = useForm<ChatRegistration>();

  async function onSubmit(registration: ChatRegistration): Promise<void> {
    setLoading(true);
    await setTimeout(() => console.log('TODO REGISTER', registration), 2000);
    setLoading(false);
  }

  function onReset() {
    registrationForm.setFieldsValue({});
  }

  return {
    loading,
    registrationForm,
    onSubmit,
    onReset
  };
}

interface ChatRegistrationHook {
  loading: boolean;
  registrationForm: FormInstance<ChatRegistration>;
  onSubmit: (registration: ChatRegistration) => Promise<void>;
  onReset: () => void;
}