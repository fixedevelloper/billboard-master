"use client";

import { FormEvent, useEffect, useState } from "react";
import useSWR from "swr";
import { useTranslations } from "next-intl";
import { Link, useRouter } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  BillboardType,
  createBillboard,
  extractErrorMessage,
  getBillboard,
  getOwnerByUserId,
} from "@/lib/api";
import { useAuth } from "@/lib/AuthProvider";

const BILLBOARD_TYPES: BillboardType[] = ["DIGITAL", "STATIC", "TRIVISION", "LED_SCREEN"];

const emptyForm = {
  title: "",
  description: "",
  type: "STATIC" as BillboardType,
  address: "",
  city: "",
  latitude: "0",
  longitude: "0",
  width: "4",
  height: "3",
  dailyRate: "50",
  currency: "XOF",
};

export default function MyBillboardsPage() {
  const t = useTranslations("owner.billboards");
  const tCommon = useTranslations("common");
  const router = useRouter();
  const { userId, isAuthenticated, hydrated, myBillboardIds, addBillboardId } = useAuth();

  useEffect(() => {
    if (hydrated && !isAuthenticated) {
      router.replace("/login");
    }
  }, [hydrated, isAuthenticated, router]);

  const { data: owner, error: ownerError } = useSWR(
    userId ? ["owner-by-user", userId] : null,
    ([, id]) => getOwnerByUserId(id),
  );

  const { data: billboards, mutate: mutateBillboards } = useSWR(
    myBillboardIds.length > 0 ? ["my-billboards", myBillboardIds.join(",")] : null,
    () => Promise.all(myBillboardIds.map((id) => getBillboard(id))),
  );

  const [form, setForm] = useState(emptyForm);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function updateField<K extends keyof typeof emptyForm>(field: K) {
    return (event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
      setForm((prev) => ({ ...prev, [field]: event.target.value }));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!owner) return;
    setSubmitting(true);
    setError(null);
    try {
      const billboard = await createBillboard({
        title: form.title,
        description: form.description || undefined,
        type: form.type,
        address: form.address,
        city: form.city,
        latitude: Number(form.latitude),
        longitude: Number(form.longitude),
        width: Number(form.width),
        height: Number(form.height),
        dailyRate: form.dailyRate,
        currency: form.currency,
        ownerId: owner.id,
      });
      addBillboardId(billboard.id);
      setForm(emptyForm);
      await mutateBillboards();
    } catch (err) {
      setError(extractErrorMessage(err, t("createTitle")));
    } finally {
      setSubmitting(false);
    }
  }

  if (!hydrated || !isAuthenticated) {
    return null;
  }

  if (ownerError) {
    return (
      <p className="mx-auto max-w-md px-4 py-12 text-center text-sm text-muted-foreground">
        <Link href="/become-owner" className="font-medium text-primary underline-offset-4 hover:underline">
          {t("title")}
        </Link>
      </p>
    );
  }

  if (!owner) {
    return <p className="mx-auto max-w-2xl px-4 py-12 text-sm text-muted-foreground">{tCommon("loading")}</p>;
  }

  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-6 px-4 py-12">
      <h1 className="text-2xl font-semibold">{t("title")}</h1>

      {billboards && billboards.length === 0 && <p className="text-sm text-muted-foreground">{t("empty")}</p>}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {billboards?.map((billboard) => (
          <Card key={billboard.id}>
            <CardHeader>
              <CardTitle className="text-base">{billboard.title}</CardTitle>
            </CardHeader>
            <CardContent className="text-sm text-muted-foreground">
              {billboard.city} — {t("status")}: {billboard.status}
            </CardContent>
          </Card>
        ))}
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">{t("createTitle")}</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
            <Input name="title" label={t("billboardTitle")} value={form.title} onChange={updateField("title")} required />
            <Input
              name="description"
              label={t("description")}
              value={form.description}
              onChange={updateField("description")}
            />
            <div className="flex flex-col gap-1.5">
              <label className="text-sm font-medium" htmlFor="type">
                {t("type")}
              </label>
              <select
                id="type"
                className="h-10 rounded-md border bg-transparent px-3 text-sm"
                value={form.type}
                onChange={updateField("type")}
              >
                {BILLBOARD_TYPES.map((value) => (
                  <option key={value} value={value}>
                    {value}
                  </option>
                ))}
              </select>
            </div>
            <div className="flex gap-3">
              <Input name="address" label={t("address")} value={form.address} onChange={updateField("address")} required />
              <Input name="city" label={t("city")} value={form.city} onChange={updateField("city")} required />
            </div>
            <div className="flex gap-3">
              <Input
                name="latitude"
                type="number"
                step="any"
                label={t("latitude")}
                value={form.latitude}
                onChange={updateField("latitude")}
                required
              />
              <Input
                name="longitude"
                type="number"
                step="any"
                label={t("longitude")}
                value={form.longitude}
                onChange={updateField("longitude")}
                required
              />
            </div>
            <div className="flex gap-3">
              <Input
                name="width"
                type="number"
                step="any"
                label={t("width")}
                value={form.width}
                onChange={updateField("width")}
                required
              />
              <Input
                name="height"
                type="number"
                step="any"
                label={t("height")}
                value={form.height}
                onChange={updateField("height")}
                required
              />
            </div>
            <div className="flex gap-3">
              <Input
                name="dailyRate"
                type="number"
                step="0.01"
                label={t("dailyRate")}
                value={form.dailyRate}
                onChange={updateField("dailyRate")}
                required
              />
              <Input
                name="currency"
                label={t("currency")}
                value={form.currency}
                onChange={updateField("currency")}
                required
              />
            </div>
            {error && <p className="text-sm text-destructive">{error}</p>}
            <Button type="submit" loading={submitting} disabled={submitting}>
              {submitting ? t("submitting") : t("submit")}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
