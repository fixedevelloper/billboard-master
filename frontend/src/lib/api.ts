import axios, { AxiosError } from "axios";
import { useAuthStore } from "@/lib/AuthProvider";

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080",
});

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Miroir de com.cscreativ.billboard.shared.api.dto.ErrorResponseDto
interface ErrorResponseDto {
  status: number;
  error: string;
  message: string;
  timestamp: string;
}

export function extractErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<ErrorResponseDto>;
    return axiosError.response?.data?.message ?? fallback;
  }
  return fallback;
}

// ---------------------------------------------------------------------------
// user / auth
// ---------------------------------------------------------------------------

// Miroir de com.cscreativ.billboard.user.api.request.RegisterRequest
export interface RegisterInput {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
}

// Miroir de com.cscreativ.billboard.user.api.response.UserResponse
export interface UserResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  status: string;
}

export async function registerUser(input: RegisterInput): Promise<UserResponse> {
  const response = await apiClient.post<UserResponse>("/api/v1/auth/register", input, {
    headers: { "Content-Type": "application/json" },
  });
  return response.data;
}

// Miroir de com.cscreativ.billboard.user.api.request.LoginRequest
export interface LoginInput {
  email: string;
  password: string;
}

// Miroir de com.cscreativ.billboard.user.api.response.LoginResponse
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
}

export async function loginUser(input: LoginInput): Promise<LoginResponse> {
  const response = await apiClient.post<LoginResponse>("/api/v1/auth/login", input, {
    headers: { "Content-Type": "application/json" },
  });
  return response.data;
}

// Miroir de com.cscreativ.billboard.user.api.response.ProfileResponse
export interface ProfileResponse {
  id: string;
  email: string;
  fullName: string;
  phoneNumber: string | null;
  status: string;
}

export async function getUserProfile(id: string): Promise<ProfileResponse> {
  const response = await apiClient.get<ProfileResponse>(`/api/v1/users/${id}`);
  return response.data;
}

// ---------------------------------------------------------------------------
// advertiser
// ---------------------------------------------------------------------------

export interface RegisterAdvertiserInput {
  userId: string;
  companyName: string;
  taxNumber: string;
  contactEmail: string;
  contactPhone: string;
}

export interface AdvertiserResponse {
  id: string;
  userId: string;
  companyName: string;
  taxNumber: string;
  contactEmail: string;
  contactPhone: string;
  status: string;
}

export async function registerAdvertiser(input: RegisterAdvertiserInput): Promise<AdvertiserResponse> {
  const response = await apiClient.post<AdvertiserResponse>("/api/v1/advertisers", input);
  return response.data;
}

export async function getAdvertiser(id: string): Promise<AdvertiserResponse> {
  const response = await apiClient.get<AdvertiserResponse>(`/api/v1/advertisers/${id}`);
  return response.data;
}

// ---------------------------------------------------------------------------
// billboard
// ---------------------------------------------------------------------------

export type BillboardType = "DIGITAL" | "STATIC" | "TRIVISION" | "LED_SCREEN";

export interface CreateBillboardInput {
  title: string;
  description?: string;
  type: BillboardType;
  address: string;
  city: string;
  latitude: number;
  longitude: number;
  width: number;
  height: number;
  dailyRate: string;
  currency: string;
  ownerId: string;
}

export interface BillboardResponse {
  id: string;
  title: string;
  description: string | null;
  type: string;
  status: string;
  address: string | null;
  city: string | null;
  latitude: number;
  longitude: number;
  width: number;
  height: number;
  dailyRate: string;
  currency: string;
  ownerId: string;
}

export async function createBillboard(input: CreateBillboardInput): Promise<BillboardResponse> {
  const response = await apiClient.post<BillboardResponse>("/api/v1/billboards", input);
  return response.data;
}

export async function searchBillboardsByCity(city: string): Promise<BillboardResponse[]> {
  const response = await apiClient.get<BillboardResponse[]>("/api/v1/billboards", { params: { city } });
  return response.data;
}

export async function getBillboard(id: string): Promise<BillboardResponse> {
  const response = await apiClient.get<BillboardResponse>(`/api/v1/billboards/${id}`);
  return response.data;
}

// ---------------------------------------------------------------------------
// booking
// ---------------------------------------------------------------------------

export interface CreateBookingInput {
  billboardId: string;
  advertiserId: string;
  startDate: string; // ISO yyyy-MM-dd
  endDate: string; // ISO yyyy-MM-dd
  dailyRate: string;
  currency: string;
}

export interface BookingResponse {
  id: string;
  billboardId: string;
  advertiserId: string;
  startDate: string;
  endDate: string;
  totalPrice: string;
  currency: string;
  status: string;
}

export async function createBooking(input: CreateBookingInput): Promise<BookingResponse> {
  const response = await apiClient.post<BookingResponse>("/api/v1/bookings", input);
  return response.data;
}

export async function getBooking(id: string): Promise<BookingResponse> {
  const response = await apiClient.get<BookingResponse>(`/api/v1/bookings/${id}`);
  return response.data;
}

export async function confirmBooking(id: string): Promise<void> {
  await apiClient.put(`/api/v1/bookings/${id}/confirm`);
}

export async function cancelBooking(id: string): Promise<void> {
  await apiClient.put(`/api/v1/bookings/${id}/cancel`);
}

export async function getBookingsByAdvertiser(advertiserId: string): Promise<BookingResponse[]> {
  const response = await apiClient.get<BookingResponse[]>(`/api/v1/bookings/advertiser/${advertiserId}`);
  return response.data;
}

// ---------------------------------------------------------------------------
// campaign
// ---------------------------------------------------------------------------

export interface CreateCampaignInput {
  bookingId: string;
  advertiserId: string;
  name: string;
  description?: string;
  mediaUrl?: string;
  fileType?: string;
  fileSize?: number;
}

export interface CampaignResponse {
  id: string;
  bookingId: string;
  advertiserId: string;
  name: string;
  description: string | null;
  mediaUrl: string | null;
  fileType: string | null;
  status: string;
  rejectionReason: string | null;
}

export async function createCampaign(input: CreateCampaignInput): Promise<CampaignResponse> {
  const response = await apiClient.post<CampaignResponse>("/api/v1/campaigns", input);
  return response.data;
}

export async function submitCampaign(id: string): Promise<void> {
  await apiClient.post(`/api/v1/campaigns/${id}/submit`);
}

export async function getCampaign(id: string): Promise<CampaignResponse> {
  const response = await apiClient.get<CampaignResponse>(`/api/v1/campaigns/${id}`);
  return response.data;
}

export async function getCampaignsByAdvertiser(advertiserId: string): Promise<CampaignResponse[]> {
  const response = await apiClient.get<CampaignResponse[]>(`/api/v1/campaigns/advertiser/${advertiserId}`);
  return response.data;
}

// ---------------------------------------------------------------------------
// creative
// ---------------------------------------------------------------------------

export interface SubmitProofInput {
  campaignId: string;
  fileUrl: string;
  width: number;
  height: number;
}

export interface CreativeProofResponse {
  id: string;
  campaignId: string;
  version: number;
  fileUrl: string;
  width: number;
  height: number;
  status: string;
  feedback: string | null;
  createdAt: string;
}

export async function submitCreativeProof(input: SubmitProofInput): Promise<CreativeProofResponse> {
  const response = await apiClient.post<CreativeProofResponse>("/api/v1/creatives/proofs", input);
  return response.data;
}

export async function getCreativeProofsByCampaign(campaignId: string): Promise<CreativeProofResponse[]> {
  const response = await apiClient.get<CreativeProofResponse[]>(`/api/v1/creatives/campaign/${campaignId}/proofs`);
  return response.data;
}

// ---------------------------------------------------------------------------
// contract
// ---------------------------------------------------------------------------

export interface CreateContractInput {
  bookingId: string;
  ownerId: string;
  advertiserId: string;
  termsAndConditions: string;
}

export interface SignContractInput {
  signerName: string;
  ipAddress: string;
}

export interface ContractResponse {
  id: string;
  bookingId: string;
  ownerId: string;
  advertiserId: string;
  termsAndConditions: string;
  status: string;
  isSignedByOwner: boolean;
  isSignedByAdvertiser: boolean;
  createdAt: string;
}

export async function createContract(input: CreateContractInput): Promise<ContractResponse> {
  const response = await apiClient.post<ContractResponse>("/api/v1/contracts", input);
  return response.data;
}

export async function publishContractForSignature(id: string): Promise<void> {
  await apiClient.post(`/api/v1/contracts/${id}/publish`);
}

export async function signContractAsAdvertiser(id: string, input: SignContractInput): Promise<void> {
  await apiClient.post(`/api/v1/contracts/${id}/sign/advertiser`, input);
}

export async function getContract(id: string): Promise<ContractResponse> {
  const response = await apiClient.get<ContractResponse>(`/api/v1/contracts/${id}`);
  return response.data;
}

export async function getContractByBooking(bookingId: string): Promise<ContractResponse> {
  const response = await apiClient.get<ContractResponse>(`/api/v1/contracts/booking/${bookingId}`);
  return response.data;
}

// ---------------------------------------------------------------------------
// payment
// ---------------------------------------------------------------------------

export type PaymentMethod = "CREDIT_CARD" | "MOBILE_MONEY" | "BANK_TRANSFER" | "CREDIT_ACCOUNT";

export interface InitiatePaymentInput {
  payerId: string;
  referenceId: string;
  amount: string;
  currency: string;
  paymentMethod: PaymentMethod;
}

export interface CompletePaymentInput {
  gatewayReference: string;
}

export interface PaymentTransactionResponse {
  id: string;
  payerId: string;
  referenceId: string;
  amount: string;
  currency: string;
  paymentMethod: string;
  status: string;
  gatewayReference: string | null;
  failureReason: string | null;
  createdAt: string;
}

export async function initiatePayment(input: InitiatePaymentInput): Promise<PaymentTransactionResponse> {
  const response = await apiClient.post<PaymentTransactionResponse>("/api/v1/payments/initiate", input);
  return response.data;
}

export async function completePayment(id: string, input: CompletePaymentInput): Promise<void> {
  await apiClient.put(`/api/v1/payments/${id}/complete`, input);
}

export async function getPaymentsByPayer(payerId: string): Promise<PaymentTransactionResponse[]> {
  const response = await apiClient.get<PaymentTransactionResponse[]>(`/api/v1/payments/payer/${payerId}`);
  return response.data;
}

// ---------------------------------------------------------------------------
// storage
// ---------------------------------------------------------------------------

export interface StoredFileResponse {
  id: string;
  originalFilename: string;
  contentType: string;
  size: number;
  publicUrl: string;
  ownerId: string;
  uploadedAt: string;
}

export async function uploadFile(file: File, ownerId: string): Promise<StoredFileResponse> {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("ownerId", ownerId);
  const response = await apiClient.post<StoredFileResponse>("/api/v1/files/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return response.data;
}

// ---------------------------------------------------------------------------
// media buyer
// ---------------------------------------------------------------------------

export interface MediaBuyerResponse {
  id: string;
  userId: string;
  companyName: string;
  taxId: string | null;
  contactEmail: string;
  phoneNumber: string | null;
  creditLimit: string;
  currentSpent: string;
  status: string;
  createdAt: string;
}

export async function listMediaBuyers(): Promise<MediaBuyerResponse[]> {
  const response = await apiClient.get<MediaBuyerResponse[]>("/api/v1/media-buyers");
  return response.data;
}

export async function getMediaBuyerByUserId(userId: string): Promise<MediaBuyerResponse> {
  const response = await apiClient.get<MediaBuyerResponse>(`/api/v1/media-buyers/user/${userId}`);
  return response.data;
}

// ---------------------------------------------------------------------------
// owner
// ---------------------------------------------------------------------------

export interface RegisterOwnerInput {
  userId: string;
  companyName: string;
  registrationNumber?: string;
  contactEmail: string;
  phoneNumber?: string;
  revenueShareRate: string;
}

export interface BillboardOwnerResponse {
  id: string;
  userId: string;
  companyName: string;
  registrationNumber: string | null;
  contactEmail: string;
  phoneNumber: string | null;
  revenueShareRate: string;
  status: string;
  createdAt: string;
}

export async function registerOwner(input: RegisterOwnerInput): Promise<BillboardOwnerResponse> {
  const response = await apiClient.post<BillboardOwnerResponse>("/api/v1/owners", input);
  return response.data;
}

export async function getOwnerByUserId(userId: string): Promise<BillboardOwnerResponse> {
  const response = await apiClient.get<BillboardOwnerResponse>(`/api/v1/owners/user/${userId}`);
  return response.data;
}
