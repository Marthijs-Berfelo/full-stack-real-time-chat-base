import { Form, FormInstance } from 'antd';
import { ChatRegistration, userService } from '../../api/user';
import { useAuth } from '../../components/AuthContext';
const { useForm } = Form;

export function useChatRegistration(): ChatRegistrationHook {
  const [registrationForm] = useForm<ChatRegistration>();
  const { registerChatAccount, loading } = useAuth();

  function onReset() {
    registrationForm.setFieldsValue({});
  }

  function onValidateNickName(nickName: string): Promise<boolean> {
    return userService.validateNickName(nickName);
  }

  return {
    loading,
    registrationForm,
    onSubmit: registerChatAccount,
    onValidateNickName,
    onReset,
  };
}

interface ChatRegistrationHook {
  loading: boolean;
  registrationForm: FormInstance<ChatRegistration>;
  onValidateNickName: (nickName: string) => Promise<boolean>;
  onSubmit: (registration: ChatRegistration) => Promise<void>;
  onReset: () => void;
}
