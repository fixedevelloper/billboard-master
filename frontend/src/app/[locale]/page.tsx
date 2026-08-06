import Image from "next/image";
import { getTranslations, setRequestLocale } from "next-intl/server";
import { Link } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import heroBillboard from "../../../public/images/hero-billboard.jpg";

export default async function HomePage({
  params,
}: {
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  setRequestLocale(locale);
  const t = await getTranslations("home");

  return (
    <div className="relative isolate flex min-h-[calc(100vh-4rem)] items-center justify-center overflow-hidden">
      <Image
        src={heroBillboard}
        alt=""
        fill
        priority
        className="object-cover"
      />
      <div className="absolute inset-0 bg-black/60" />
      <div className="relative mx-auto flex max-w-2xl flex-col items-center gap-6 px-4 text-center text-white">
        <h1 className="text-4xl font-bold tracking-tight sm:text-5xl">{t("heroTitle")}</h1>
        <p className="text-lg text-white/80">{t("heroSubtitle")}</p>
        <div className="flex gap-3">
          <Button asChild size="lg">
            <Link href="/register">{t("ctaRegister")}</Link>
          </Button>
          <Button asChild size="lg" variant="outline" className="border-white text-white hover:bg-white/10 hover:text-white">
            <Link href="/login">{t("ctaLogin")}</Link>
          </Button>
        </div>
      </div>
    </div>
  );
}
