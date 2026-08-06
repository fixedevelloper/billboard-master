"use client";

import { FormEvent, useState } from "react";
import { useTranslations } from "next-intl";
import { Link, useRouter } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { extractErrorMessage, loginUser } from "@/lib/api";
import { useAuth } from "@/lib/AuthProvider";

export default function LoginPage() {
  const t = useTranslations("auth.login");
  const tCommon = useTranslations("common");
  const router = useRouter();
  const { login } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const { accessToken } = await loginUser({ email, password });
      login(accessToken);
      router.push("/profile");
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
          <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
            <Input
              name="email"
              type="email"
              label={tCommon("email")}
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
            <Input
              name="password"
              type="password"
              label={tCommon("password")}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
              error={error ?? undefined}
            />
            <Button type="submit" loading={submitting} disabled={submitting}>
              {submitting ? t("submitting") : t("submit")}
            </Button>
            <p className="text-center text-sm text-muted-foreground">
              {t("noAccount")}{" "}
              <Link href="/register" className="font-medium text-primary underline-offset-4 hover:underline">
                {t("createAccount")}
              </Link>
            </p>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
