import React, { JSX } from 'react';
import { Badge, Button, Col, Form, FormInstance, Input, Row } from 'antd';
import { useChat } from './ChatContext';
import { useTranslation } from 'react-i18next';
const { useForm } = Form;
export function MessageForm(): JSX.Element {
  const { t } = useTranslation(['chat']);
  const { sendingMessage, totalNewMessages, disableMessageForm, onShowInbox } = useChat();
  const { messageForm, onSubmit, onReset } = useMessageForm();

  return (
    <Col className="w-full h-full">
      <Row justify="center" className="m-3">
        <Badge count={totalNewMessages}>
          <Button onClick={onShowInbox} className="text-lg">
            {t('chat:section.inbox')}
          </Button>
        </Badge>
      </Row>
      <Form
        form={messageForm}
        layout="vertical"
        className="flex flex-row flex-nowrap"
        onFinish={onSubmit}
        onReset={onReset}
        disabled={disableMessageForm}
      >
        <Form.Item name="message" required>
          <Input />
        </Form.Item>
        <Button htmlType="submit" type="primary" disabled={sendingMessage}>
          {t('chat:message.send')}
        </Button>
        <Button htmlType="reset" type="default" disabled={sendingMessage}>
          {t('chat:message.reset')}
        </Button>
      </Form>
    </Col>
  );
}

function useMessageForm(): MessageFormHook {
  const [messageForm] = useForm<NewMessageForm>();
  const { onSendMessage } = useChat();

  function onReset(): void {
    messageForm.setFieldsValue({});
  }

  async function onSubmit(data: NewMessageForm): Promise<void> {
    await onSendMessage(data.message);
  }

  return {
    messageForm,
    onReset,
    onSubmit,
  };
}

interface MessageFormHook {
  messageForm: FormInstance<NewMessageForm>;
  onReset: () => void;
  onSubmit: (data: NewMessageForm) => Promise<void>;
}

interface NewMessageForm {
  message: string;
}
