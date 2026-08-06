"use client";

import { FormEvent, useState } from "react";
import { useTranslations } from "next-intl";
import { Link } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { extractErrorMessage, registerUser } from "@/lib/api";

export default function RegisterPage() {
  const t = useTranslations("auth.register");
  const tCommon = useTranslations("common");

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    password: "",
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  function updateField(field: keyof typeof form) {
    return (event: React.ChangeEvent<HTMLInputElement>) =>
      setForm((prev) => ({ ...prev, [field]: event.target.value }));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await registerUser({
        email: form.email,
        password: form.password,
        firstName: form.firstName,
        lastName: form.lastName,
        phoneNumber: form.phoneNumber || undefined,
      });
      setSuccess(true);
    } catch (err) {
      setError(extractErrorMessage(err, t("title")));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="mx-auto flex max-w-md flex-col gap-6 px-4 py-16">
      <Card>
        <CardHeader>
          <CardTitle>{t("title")}</CardTitle>
          <CardDescription>{t("subtitle")}</CardDescription>
        </CardHeader>
        <CardContent>
          {success ? (
            <div className="flex flex-col gap-4">
              <p className="text-sm text-foreground">{t("success")}</p>
              <Button asChild>
                <Link href="/login">{t("goToLogin")}</Link>
              </Button>
            </div>
          ) : (
            <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
              <div className="flex gap-3">
                <Input
                  name="firstName"
                  label={t("firstName")}
                  value={form.firstName}
                  onChange={updateField("firstName")}
                  required
                />
                <Input
                  name="lastName"
                  label={t("lastName")}
                  value={form.lastName}
                  onChange={updateField("lastName")}
                  required
                />
              </div>
              <Input
                name="email"
                type="email"
                label={tCommon("email")}
                value={form.email}
                onChange={updateField("email")}
                required
              />
              <Input
                name="phoneNumber"
                type="tel"
                label={t("phone")}
                value={form.phoneNumber}
                onChange={updateField("phoneNumber")}
              />
              <Input
                name="password"
                type="password"
                label={tCommon("password")}
                value={form.password}
                onChange={updateField("password")}
                required
                error={error ?? undefined}
              />
              <Button type="submit" loading={submitting} disabled={submitting}>
                {submitting ? t("submitting") : t("submit")}
              </Button>
              <p className="text-center text-sm text-muted-foreground">
                {t("haveAccount")}{" "}
                <Link href="/login" className="font-medium text-primary underline-offset-4 hover:underline">
                  {t("goToLogin")}
                </Link>
              </p>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
